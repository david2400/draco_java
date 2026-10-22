package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.variant;

import com.essenza.draco.modules.catalog.application.output.repository.VariantMatrixStore;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductChildEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductSkuEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.product_child.JpaProductChildRepository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku.JpaProductSkuRepository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku.ProductSkuSynchronizer;
import com.essenza.draco.shared.exceptions.NotFoundException;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Persistencia del generador: crea y edita variantes ({@code product_childs}), sincroniza sus SKU
 * con {@link ProductSkuSynchronizer} y guarda ejes e imágenes por valor. El historial de un SKU
 * (stock, movimientos, reservas, ventas) se consulta en las tablas de inventario y ventas, solo lectura.
 */
@Repository
public class VariantMatrixStoreAdapter implements VariantMatrixStore {

    private final JpaProductChildRepository children;
    private final JpaProductSkuRepository skus;
    private final ProductSkuSynchronizer synchronizer;
    private final EntityManager em;

    public VariantMatrixStoreAdapter(JpaProductChildRepository children, JpaProductSkuRepository skus,
                                     ProductSkuSynchronizer synchronizer, EntityManager em) {
        this.children = children;
        this.skus = skus;
        this.synchronizer = synchronizer;
        this.em = em;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<ProductInfo> product(Long productId) {
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT id_product, name, unit_price, real_price, template_id, is_combo
                          FROM products WHERE id_product = :id AND deleted = 0""")
                .setParameter("id", productId).getResultList();
        return rows.stream().findFirst().map(r -> new ProductInfo(lng(r[0]), (String) r[1], money(r[2]), money(r[3]),
                lng(r[4]), bool(r[5])));
    }

    @Override
    public List<VariantRecord> variants(Long productId) {
        List<ProductChildEntity> list = new ArrayList<>(children.findByProductId(productId));
        list.sort(Comparator.comparing(ProductChildEntity::getId));
        Map<Long, ProductSkuEntity> skuByChild = new HashMap<>();
        skus.findByProductId(productId).stream().filter(s -> s.getLegacyChildId() != null)
                .forEach(s -> skuByChild.put(s.getLegacyChildId(), s));
        Map<Long, Integer> stock = stockBySku(skuByChild.values().stream().map(ProductSkuEntity::getId).toList());
        List<VariantRecord> out = new ArrayList<>();
        for (ProductChildEntity child : list) {
            ProductSkuEntity sku = skuByChild.get(child.getId());
            out.add(new VariantRecord(child.getId(), sku == null ? null : sku.getId(), sku == null ? null : sku.getCode(),
                    child.getName(), child.getUnitPrice(), child.getCostPrice(),
                    sku == null ? null : sku.getCompareAtPrice(), sku == null ? null : sku.getBarcode(), child.getImageUrl(),
                    Boolean.TRUE.equals(child.getAvailable()),
                    sku == null ? 0 : stock.getOrDefault(sku.getId(), 0)));
        }
        return out;
    }

    @Override
    public Long createChild(Long productId, ChildData data) {
        ProductChildEntity child = new ProductChildEntity();
        child.setProductId(productId);
        child.setStock(0); // el stock lo gobierna el inventario (entrada inicial aparte)
        apply(child, data);
        return children.saveAndFlush(child).getId();
    }

    @Override
    public void updateChild(Long childId, ChildData data) {
        ProductChildEntity child = child(childId);
        apply(child, data);
        children.save(child);
    }

    @Override
    public void setChildActive(Long childId, boolean active) {
        ProductChildEntity child = child(childId);
        child.setAvailable(active);
        children.save(child);
    }

    @Override
    public void deleteChild(Long childId) {
        children.deleteById(childId);
        children.flush();
    }

    @Override
    public Map<Long, Long> syncSkus(Long productId) {
        children.flush();
        return synchronizer.sync(productId).skuByChildId();
    }

    @Override
    public void updateSkuExtras(Long skuId, String code, String barcode, BigDecimal compareAt) {
        ProductSkuEntity sku = skus.findById(skuId).orElseThrow(() -> new NotFoundException("SKU no encontrado: " + skuId));
        if (code != null) {
            sku.setCode(code);
        }
        sku.setBarcode(barcode);
        sku.setCompareAtPrice(compareAt);
        skus.saveAndFlush(sku);
    }

    @Override
    public boolean codeTaken(String code, Long excludeSkuId) {
        // El índice único de code cuenta también las filas borradas.
        Number n = (Number) em.createNativeQuery("SELECT COUNT(*) FROM product_skus WHERE code = :code AND id_sku <> :id")
                .setParameter("code", code).setParameter("id", excludeSkuId == null ? -1L : excludeSkuId).getSingleResult();
        return n.longValue() > 0;
    }

    @Override
    public boolean barcodeTaken(String barcode, Collection<Long> excludeSkuIds) {
        List<Long> exclude = excludeSkuIds == null || excludeSkuIds.isEmpty() ? List.of(-1L) : List.copyOf(excludeSkuIds);
        Number n = (Number) em.createNativeQuery("""
                        SELECT COUNT(*) FROM product_skus
                         WHERE deleted = 0 AND barcode = :barcode AND id_sku NOT IN (:ids)""")
                .setParameter("barcode", barcode).setParameter("ids", exclude).getSingleResult();
        return n.longValue() > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Long> withHistory(Collection<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return Set.of();
        }
        Set<Long> out = new HashSet<>();
        Map<String, String> queries = new LinkedHashMap<>();
        queries.put("stock_items", "SELECT DISTINCT sku_id FROM stock_items WHERE sku_id IN (:ids) AND (on_hand > 0 OR reserved > 0)");
        queries.put("inventory_movements", "SELECT DISTINCT sku_id FROM inventory_movements WHERE sku_id IN (:ids) AND deleted = 0");
        queries.put("stock_reservations", "SELECT DISTINCT sku_id FROM stock_reservations WHERE sku_id IN (:ids)");
        queries.put("product_orders", "SELECT DISTINCT sku_id FROM product_orders WHERE sku_id IN (:ids)");
        queries.forEach((table, sql) -> {
            // Una tabla de otro módulo puede no existir en una base parcial: esa fuente se omite.
            if (tableExists(table)) {
                ((List<Object>) em.createNativeQuery(sql).setParameter("ids", skuIds).getResultList()).stream()
                        .filter(java.util.Objects::nonNull)
                        .forEach(value -> out.add(((Number) value).longValue()));
            }
        });
        return out;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AxisRecord> axes(Long productId) {
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT attribute_id, position, image_axis FROM product_variant_axes
                         WHERE product_id = :id ORDER BY position, attribute_id""")
                .setParameter("id", productId).getResultList();
        return rows.stream().map(r -> new AxisRecord(lng(r[0]), ((Number) r[1]).intValue(), bool(r[2]))).toList();
    }

    @Override
    public void replaceAxes(Long productId, List<AxisRecord> axes) {
        em.createNativeQuery("DELETE FROM product_variant_axes WHERE product_id = :id").setParameter("id", productId).executeUpdate();
        for (AxisRecord axis : axes) {
            em.createNativeQuery("""
                            INSERT INTO product_variant_axes (product_id, attribute_id, position, image_axis)
                            VALUES (:p, :a, :pos, :img)""")
                    .setParameter("p", productId).setParameter("a", axis.attributeId())
                    .setParameter("pos", axis.position()).setParameter("img", axis.imageAxis() ? 1 : 0).executeUpdate();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, String> optionImages(Long productId) {
        List<Object[]> rows = em.createNativeQuery("SELECT option_id, image_url FROM product_option_images WHERE product_id = :id")
                .setParameter("id", productId).getResultList();
        Map<Long, String> out = new LinkedHashMap<>();
        rows.forEach(r -> out.put(lng(r[0]), (String) r[1]));
        return out;
    }

    @Override
    public void replaceOptionImages(Long productId, Map<Long, String> images) {
        em.createNativeQuery("DELETE FROM product_option_images WHERE product_id = :id").setParameter("id", productId).executeUpdate();
        images.forEach((optionId, url) -> em.createNativeQuery("""
                        INSERT INTO product_option_images (product_id, option_id, image_url) VALUES (:p, :o, :u)""")
                .setParameter("p", productId).setParameter("o", optionId).setParameter("u", url).executeUpdate());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long ensureOption(Long attributeId, String value) {
        List<Object> found = em.createNativeQuery("""
                        SELECT id_option FROM attribute_options WHERE attribute_id = :a AND LOWER(value) = LOWER(:v)""")
                .setParameter("a", attributeId).setParameter("v", value).getResultList();
        if (!found.isEmpty()) {
            return ((Number) found.get(0)).longValue();
        }
        Number next = (Number) em.createNativeQuery("SELECT COALESCE(MAX(position), -1) + 1 FROM attribute_options WHERE attribute_id = :a")
                .setParameter("a", attributeId).getSingleResult();
        em.createNativeQuery("""
                        INSERT INTO attribute_options (attribute_id, value, position, created_at, updated_at)
                        VALUES (:a, :v, :p, NOW(6), NOW(6))""")
                .setParameter("a", attributeId).setParameter("v", value).setParameter("p", next.intValue()).executeUpdate();
        // Versión del atributo: invalida lecturas en caché/optimistas del formulario de atributos.
        em.createNativeQuery("UPDATE attributes SET version = version + 1, updated_at = NOW(6) WHERE id_attribute = :a")
                .setParameter("a", attributeId).executeUpdate();
        em.flush();
        em.clear();
        return ((Number) em.createNativeQuery("""
                        SELECT id_option FROM attribute_options WHERE attribute_id = :a AND LOWER(value) = LOWER(:v)""")
                .setParameter("a", attributeId).setParameter("v", value).getSingleResult()).longValue();
    }

    private ProductChildEntity child(Long childId) {
        return children.findById(childId).orElseThrow(() -> new NotFoundException("Variante no encontrada: " + childId));
    }

    private static void apply(ProductChildEntity child, ChildData data) {
        child.setName(data.name());
        child.setUnitPrice(data.price());
        child.setCostPrice(data.cost());
        child.setImageUrl(data.imageUrl());
        child.setAvailable(data.active());
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> stockBySku(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Integer> out = new HashMap<>();
        if (!tableExists("stock_items")) {
            return out;
        }
        ((List<Object[]>) em.createNativeQuery("""
                        SELECT sku_id, COALESCE(SUM(on_hand), 0) FROM stock_items
                         WHERE deleted = 0 AND sku_id IN (:ids) GROUP BY sku_id""")
                .setParameter("ids", skuIds).getResultList())
                .forEach(r -> out.put(lng(r[0]), ((Number) r[1]).intValue()));
        return out;
    }

    private boolean tableExists(String table) {
        Number n = (Number) em.createNativeQuery("""
                        SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = :t""")
                .setParameter("t", table).getSingleResult();
        return n.intValue() > 0;
    }

    private static Long lng(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private static BigDecimal money(Object value) {
        if (value == null) {
            return null;
        }
        return value instanceof BigDecimal b ? b : new BigDecimal(value.toString());
    }

    private static boolean bool(Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof byte[] bytes) {
            return bytes.length > 0 && bytes[0] != 0;
        }
        return value instanceof Number n && n.intValue() != 0;
    }
}

package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.shared.common.catalog.SkuPricing;
import com.essenza.draco.shared.exceptions.NotFoundException;

import jakarta.persistence.EntityManager;

/**
 * Precio y datos vigentes de un SKU para las órdenes ({@link SkuPricing}).
 * Un producto simple o combo usa su SKU por defecto; uno con variantes exige el SKU.
 */
@Component
public class SkuPricingAdapter implements SkuPricing {

    private static final String SELECT = """
            SELECT s.id_sku, s.product_id, s.code, p.name, s.name, s.price, s.active, p.status, s.legacy_child_id
              FROM product_skus s
              JOIN products p ON p.id_product = s.product_id AND p.deleted = 0
             WHERE s.deleted = 0
            """;

    private final EntityManager em;

    public SkuPricingAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public SkuSnapshot resolve(Long productId, Long skuId) {
        List<Object[]> rows;
        if (skuId != null) {
            rows = em.createNativeQuery(SELECT + " AND s.id_sku = :sku").setParameter("sku", skuId).getResultList();
            if (rows.isEmpty()) {
                throw new NotFoundException("SKU no encontrado: " + skuId);
            }
            Long owner = ((Number) rows.get(0)[1]).longValue();
            if (productId != null && !Objects.equals(owner, productId)) {
                throw new IllegalArgumentException("El SKU " + skuId + " no pertenece al producto " + productId);
            }
            return toSnapshot(rows.get(0));
        }
        if (productId == null) {
            throw new IllegalArgumentException("Indica el SKU (sku_id) o el producto (product_id) de la línea");
        }
        List<?> type = em.createNativeQuery("SELECT product_type FROM products WHERE id_product = :p AND deleted = 0")
                .setParameter("p", productId).getResultList();
        if (type.isEmpty()) {
            throw new NotFoundException("Producto no encontrado: " + productId);
        }
        if ("VARIANT".equals(type.get(0))) {
            throw new IllegalArgumentException("El producto " + productId + " tiene variantes: indica el SKU (sku_id) de la línea");
        }
        rows = em.createNativeQuery(SELECT + " AND s.product_id = :p AND s.legacy_child_id IS NULL"
                        + " ORDER BY s.is_default DESC, s.id_sku LIMIT 1")
                .setParameter("p", productId).getResultList();
        if (rows.isEmpty()) {
            throw new NotFoundException("El producto " + productId + " no tiene SKU por defecto");
        }
        return toSnapshot(rows.get(0));
    }

    private static SkuSnapshot toSnapshot(Object[] row) {
        String productName = (String) row[3];
        String skuName = (String) row[4];
        boolean variant = row[8] != null;
        String name = variant && skuName != null && !skuName.isBlank() && !skuName.equals(productName)
                ? productName + " · " + skuName
                : productName;
        boolean active = toBoolean(row[6]);
        boolean sellable = active && "ACTIVE".equals(row[7]);
        BigDecimal price = row[5] == null ? BigDecimal.ZERO : new BigDecimal(row[5].toString());
        return new SkuSnapshot(((Number) row[0]).longValue(), ((Number) row[1]).longValue(), (String) row[2], name,
                price, sellable);
    }

    private static boolean toBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        if (value instanceof byte[] bytes) {
            return bytes.length > 0 && bytes[0] != 0;
        }
        return value != null && Boolean.parseBoolean(value.toString());
    }
}

package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku;

import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductChildEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductImageEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductSkuEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.product_child.JpaProductChildRepository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.products.JpaProductRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Capa de compatibilidad de la Fase 2: mantiene {@code product_skus} y
 * {@code product_images} alineadas con el modelo heredado ({@code products} +
 * {@code product_childs}) cada vez que este cambia.
 *
 * <ul>
 *   <li>Sin variantes (simple o combo): un SKU por defecto ({@code SKU-000123}).</li>
 *   <li>Con variantes: un SKU por variante ({@code SKU-000123-45}); el de menor id es el
 *       predeterminado. El SKU por defecto anterior se desactiva (no se borra) y se
 *       reutiliza si el producto vuelve a no tener variantes.</li>
 *   <li>Imagen principal del producto en la posición 0 y la de cada variante ligada a su SKU.</li>
 *   <li>{@code products.product_type} se recalcula (SIMPLE / VARIANT / COMBO).</li>
 * </ul>
 *
 * Es idempotente: ejecutarlo dos veces no crea duplicados.
 */
@Component
public class ProductSkuSynchronizer {

    private final JpaProductRepository products;
    private final JpaProductChildRepository children;
    private final JpaProductSkuRepository skus;
    private final JpaProductImageRepository images;

    public ProductSkuSynchronizer(JpaProductRepository products,
                                  JpaProductChildRepository children,
                                  JpaProductSkuRepository skus,
                                  JpaProductImageRepository images) {
        this.products = products;
        this.children = children;
        this.skus = skus;
        this.images = images;
    }

    public static String defaultCode(Long productId) {
        return String.format("SKU-%06d", productId);
    }

    public static String variantCode(Long productId, Long childId) {
        return String.format("SKU-%06d-%d", productId, childId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SkuSyncResult sync(Long productId) {
        Optional<ProductEntity> found = products.findById(productId);
        if (found.isEmpty()) {
            removeAll(productId);
            return SkuSyncResult.empty();
        }
        Long defaultSkuId = null;
        ProductEntity product = found.get();
        List<ProductChildEntity> variants = new ArrayList<>(children.findByProductId(productId));
        variants.sort(Comparator.comparing(ProductChildEntity::getId));

        List<ProductSkuEntity> existing = skus.findByProductId(productId);
        Map<Long, ProductSkuEntity> byChild = new HashMap<>();
        ProductSkuEntity defaultSku = null;
        for (ProductSkuEntity sku : existing) {
            if (sku.getLegacyChildId() != null) {
                byChild.put(sku.getLegacyChildId(), sku);
            } else if (defaultSku == null) {
                defaultSku = sku;
            } else {
                skus.delete(sku); // duplicado inesperado
            }
        }

        Map<Long, ProductSkuEntity> skuByChild = new HashMap<>();
        if (variants.isEmpty()) {
            byChild.values().forEach(skus::delete);
            ProductSkuEntity sku = defaultSku != null ? defaultSku : ProductSkuEntity.builder()
                    .productId(productId).code(uniqueCode(defaultCode(productId))).build();
            applyProduct(sku, product);
            sku.setName(product.getName());
            sku.setDescription(product.getDescription());
            sku.setPrice(nonNull(product.getUnitPrice()));
            sku.setImageUrl(product.getImageUrl());
            sku.setIsDefault(true);
            sku.setActive(Boolean.TRUE.equals(product.getAvailable()));
            defaultSkuId = skus.save(sku).getId();
        } else {
            if (defaultSku != null && (Boolean.TRUE.equals(defaultSku.getActive()) || Boolean.TRUE.equals(defaultSku.getIsDefault()))) {
                defaultSku.setActive(false);
                defaultSku.setIsDefault(false);
                skus.save(defaultSku);
            }
            Long firstChildId = variants.get(0).getId();
            for (ProductChildEntity child : variants) {
                ProductSkuEntity sku = byChild.remove(child.getId());
                if (sku == null) {
                    sku = ProductSkuEntity.builder()
                            .productId(productId)
                            .code(uniqueCode(variantCode(productId, child.getId())))
                            .legacyChildId(child.getId())
                            .build();
                }
                applyProduct(sku, product);
                sku.setName(child.getName());
                sku.setDescription(child.getDescription());
                sku.setPrice(nonNull(child.getUnitPrice()));
                sku.setImageUrl(child.getImageUrl());
                sku.setIsDefault(Objects.equals(child.getId(), firstChildId));
                sku.setActive(Boolean.TRUE.equals(child.getAvailable()));
                skuByChild.put(child.getId(), skus.save(sku));
            }
            byChild.values().forEach(skus::delete); // variantes eliminadas
        }

        String type = Boolean.TRUE.equals(product.getIsCombo()) ? "COMBO" : variants.isEmpty() ? "SIMPLE" : "VARIANT";
        if (!type.equals(product.getProductType())) {
            product.setProductType(type);
            products.save(product);
        }
        syncImages(product, variants, skuByChild);
        Map<Long, Long> ids = new HashMap<>();
        skuByChild.forEach((childId, sku) -> ids.put(childId, sku.getId()));
        return new SkuSyncResult(defaultSkuId, ids);
    }

    private void syncImages(ProductEntity product, List<ProductChildEntity> variants, Map<Long, ProductSkuEntity> skuByChild) {
        Long productId = product.getId();
        List<ProductImageEntity> current = images.findByProductId(productId);
        ProductImageEntity main = current.stream().filter(i -> i.getSkuId() == null && i.getPosition() == 0).findFirst().orElse(null);
        upsertImage(main, productId, null, 0, product.getImageUrl(), product.getName());

        Map<Long, ProductImageEntity> bySku = new HashMap<>();
        current.stream().filter(i -> i.getSkuId() != null).forEach(i -> bySku.putIfAbsent(i.getSkuId(), i));
        int position = 1;
        for (ProductChildEntity child : variants) {
            ProductSkuEntity sku = skuByChild.get(child.getId());
            if (sku == null) {
                continue;
            }
            upsertImage(bySku.remove(sku.getId()), productId, sku.getId(), position++, child.getImageUrl(), child.getName());
        }
        bySku.values().forEach(images::delete);
    }

    private void upsertImage(ProductImageEntity image, Long productId, Long skuId, int position, String url, String alt) {
        if (url == null || url.isBlank()) {
            if (image != null) {
                images.delete(image);
            }
            return;
        }
        ProductImageEntity target = image != null ? image : ProductImageEntity.builder().productId(productId).skuId(skuId).build();
        target.setUrl(url.trim());
        target.setPosition(position);
        target.setAltText(alt);
        images.save(target);
    }

    /** Código libre: si ya existe (aunque esté borrado lógicamente) se añade un sufijo. */
    private String uniqueCode(String base) {
        String candidate = base;
        int suffix = 2;
        while (skus.countByCodeIncludingDeleted(candidate) > 0) {
            candidate = base + "-R" + suffix++;
        }
        return candidate;
    }

    private void applyProduct(ProductSkuEntity sku, ProductEntity product) {
        sku.setCostPrice(product.getRealPrice());
        sku.setWeight(product.getWeight());
        sku.setLength(product.getLength());
        sku.setWidth(product.getWidth());
        sku.setHeight(product.getHeight());
    }

    /** Producto eliminado: sus SKUs e imágenes también (borrado lógico). */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAll(Long productId) {
        skus.deleteAll(skus.findByProductId(productId));
        images.deleteAll(images.findByProductId(productId));
    }

    private static BigDecimal nonNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}

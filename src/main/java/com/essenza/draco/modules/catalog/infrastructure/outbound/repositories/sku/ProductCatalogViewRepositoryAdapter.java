package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku;

import com.essenza.draco.modules.catalog.application.dto.product.ProductImageDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductSkuDto;
import com.essenza.draco.modules.catalog.application.output.repository.ProductCatalogViewRepository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductImageEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductSkuEntity;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class ProductCatalogViewRepositoryAdapter implements ProductCatalogViewRepository {

    private final JpaProductSkuRepository skus;
    private final JpaProductImageRepository images;

    public ProductCatalogViewRepositoryAdapter(JpaProductSkuRepository skus, JpaProductImageRepository images) {
        this.skus = skus;
        this.images = images;
    }

    @Override
    public Map<Long, List<ProductSkuDto>> findSkusByProductIds(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Map.of();
        }
        return skus.findByProductIdIn(productIds).stream()
                .filter(ProductCatalogViewRepositoryAdapter::isVisible)
                .sorted(Comparator.comparing((ProductSkuEntity s) -> !Boolean.TRUE.equals(s.getIsDefault()))
                        .thenComparing(ProductSkuEntity::getId))
                .collect(Collectors.groupingBy(ProductSkuEntity::getProductId,
                        Collectors.mapping(ProductCatalogViewRepositoryAdapter::toDto, Collectors.toList())));
    }

    @Override
    public Map<Long, List<ProductImageDto>> findImagesByProductIds(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Map.of();
        }
        return images.findByProductIdIn(productIds).stream()
                .sorted(Comparator.comparing(ProductImageEntity::getPosition).thenComparing(ProductImageEntity::getId))
                .collect(Collectors.groupingBy(ProductImageEntity::getProductId,
                        Collectors.mapping(ProductCatalogViewRepositoryAdapter::toDto, Collectors.toList())));
    }

    /** El SKU por defecto "aparcado" (producto con variantes) no se muestra. */
    static boolean isVisible(ProductSkuEntity sku) {
        return sku.getLegacyChildId() != null || Boolean.TRUE.equals(sku.getIsDefault()) || Boolean.TRUE.equals(sku.getActive());
    }

    static ProductSkuDto toDto(ProductSkuEntity sku) {
        return ProductSkuDto.builder()
                .id(sku.getId())
                .code(sku.getCode())
                .name(sku.getName())
                .price(sku.getPrice())
                .costPrice(sku.getCostPrice())
                .compareAtPrice(sku.getCompareAtPrice())
                .barcode(sku.getBarcode())
                .imageUrl(sku.getImageUrl())
                .isDefault(sku.getIsDefault())
                .active(sku.getActive())
                .variantId(sku.getLegacyChildId())
                .build();
    }

    static ProductImageDto toDto(ProductImageEntity image) {
        return ProductImageDto.builder()
                .id(image.getId())
                .url(image.getUrl())
                .position(image.getPosition())
                .altText(image.getAltText())
                .skuId(image.getSkuId())
                .build();
    }
}

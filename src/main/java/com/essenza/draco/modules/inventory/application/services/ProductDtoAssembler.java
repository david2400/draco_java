package com.essenza.draco.modules.inventory.application.services;

import com.essenza.draco.modules.inventory.domain.dto.product.ProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductVariantDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductBundleItemDto;
import com.essenza.draco.modules.inventory.domain.model.Product;
import com.essenza.draco.modules.inventory.domain.model.ProductType;
import com.essenza.draco.modules.inventory.domain.model.Variant;
import com.essenza.draco.modules.inventory.domain.model.BundleItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductDtoAssembler {

    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId().getValue())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStockInfo().getOnHand())
                .realPrice(product.getRealPrice())
                .unitPrice(product.getUnitPrice())
                .length(product.getLength())
                .width(product.getWidth())
                .height(product.getHeight())
                .weight(product.getWeight())
                .imageUrl(product.getImageUrl())
                .available(product.isAvailable())
                .brandId(product.getBrandId())
                .categoryId(product.getCategoryId())
                .subcategoryId(product.getSubcategoryId())
                .supplierId(product.getSupplierId())
                .isCombo(product.getType() == ProductType.COMBO)
                .variants(mapVariants(product.getVariants()))
                .bundleItems(mapBundleItems(product.getBundleItems()))
                .build();
    }

    private List<ProductVariantDto> mapVariants(List<Variant> variants) {
        return variants.stream()
                .map(variant -> ProductVariantDto.builder()
                        .id(variant.getId() == null ? null : variant.getId().getValue())
                        .name(variant.getName())
                        .description(variant.getDescription())
                        .stock(variant.getStockInfo().getOnHand())
                        .unitPrice(variant.getUnitPrice())
                        .imageUrl(variant.getImageUrl())
                        .available(variant.isAvailable())
                        .build())
                .toList();
    }

    private List<ProductBundleItemDto> mapBundleItems(List<BundleItem> items) {
        return items.stream()
                .map(item -> ProductBundleItemDto.builder()
                        .productId(item.getProductId().getValue())
                        .quantity(item.getQuantity())
                        .contributionPercentage(item.getContributionPercentage())
                        .build())
                .toList();
    }
}

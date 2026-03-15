package com.essenza.draco.modules.inventory.application.services;

import com.essenza.draco.modules.inventory.domain.command.ProductBundleItemCommand;
import com.essenza.draco.modules.inventory.domain.command.ProductCommand;
import com.essenza.draco.modules.inventory.domain.command.ProductVariantCommand;
import com.essenza.draco.modules.inventory.domain.dto.product.CreateProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductBundleItemDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductVariantDto;
import com.essenza.draco.modules.inventory.domain.dto.product.UpdateProductDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductCommandAssembler {

    public ProductCommand fromCreateDto(CreateProductDto dto) {
        return toCommand(dto, null);
    }

    public ProductCommand fromUpdateDto(UpdateProductDto dto) {
        return toCommand(dto, dto.getId());
    }

    private ProductCommand toCommand(CreateProductDto dto, Long id) {
        return ProductCommand.builder()
                .id(id)
                .name(dto.getName())
                .description(dto.getDescription())
                .stock(dto.getStock())
                .realPrice(dto.getRealPrice())
                .unitPrice(dto.getUnitPrice())
                .length(dto.getLength())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .weight(dto.getWeight())
                .imageUrl(dto.getImageUrl())
                .available(dto.getAvailable())
                .brandId(dto.getBrandId())
                .categoryId(dto.getCategoryId())
                .subcategoryId(dto.getSubcategoryId())
                .supplierId(dto.getSupplierId())
                .isCombo(Boolean.TRUE.equals(dto.getIsCombo()))
                .variants(mapVariants(dto.getVariants()))
                .bundleItems(mapBundleItems(dto.getBundleItems()))
                .build();
    }

    private List<ProductVariantCommand> mapVariants(List<ProductVariantDto> variants) {
        if (variants == null || variants.isEmpty()) {
            return List.of();
        }
        return variants.stream()
                .map(variant -> ProductVariantCommand.builder()
                        .id(variant.getId())
                        .name(variant.getName())
                        .description(variant.getDescription())
                        .stock(variant.getStock())
                        .unitPrice(variant.getUnitPrice())
                        .imageUrl(variant.getImageUrl())
                        .available(variant.getAvailable())
                        .build())
                .toList();
    }

    private List<ProductBundleItemCommand> mapBundleItems(List<ProductBundleItemDto> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return items.stream()
                .map(item -> ProductBundleItemCommand.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .contributionPercentage(item.getContributionPercentage())
                        .build())
                .toList();
    }
}

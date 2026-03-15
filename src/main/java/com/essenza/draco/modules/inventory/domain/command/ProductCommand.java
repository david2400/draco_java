package com.essenza.draco.modules.inventory.domain.command;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductCommand {
    Long id;
    String name;
    String description;
    Integer stock;
    BigDecimal realPrice;
    BigDecimal unitPrice;
    Double length;
    Double width;
    Double height;
    Double weight;
    String imageUrl;
    Boolean available;
    Long brandId;
    Long categoryId;
    Long subcategoryId;
    Long supplierId;
    Boolean isCombo;
    List<ProductVariantCommand> variants;
    List<ProductBundleItemCommand> bundleItems;
}

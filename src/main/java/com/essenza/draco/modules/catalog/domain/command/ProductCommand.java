package com.essenza.draco.modules.catalog.domain.command;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
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
    java.math.BigDecimal netContent;
    Long netContentUnitId;
    String imageUrl;
    Boolean available;
    /** DRAFT | ACTIVE | INACTIVE | ARCHIVED; si es null se deriva de {@code available}. */
    String status;
    String slug;
    Long brandId;
    Long categoryId;
    Long subcategoryId;
    Long supplierId;
    Boolean isCombo;
    List<ProductVariantCommand> variants;
    List<ProductBundleItemCommand> bundleItems;
}

package com.essenza.draco.modules.inventory.domain.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    @PositiveOrZero
    private Integer stock;
    @NotNull
    @Positive
    private BigDecimal realPrice;
    @NotNull
    @Positive
    private BigDecimal unitPrice;
    @NotNull
    @Positive
    private Double length;
    @NotNull
    @Positive
    private Double width;
    @NotNull
    @Positive
    private Double height;
    @NotNull
    @Positive
    private Double weight;
    private String imageUrl;
    private Boolean available;
    @NotNull
    @Positive
    private Long brandId;
    @NotNull
    @Positive
    private Long categoryId;
    @NotNull
    @Positive
    private Long subcategoryId;
    @NotNull
    @Positive
    private Long supplierId;

    @Builder.Default
    private Boolean isCombo = false;

    @Valid
    @Builder.Default
    private List<ProductVariantDto> variants = List.of();

    @Valid
    @Builder.Default
    private List<ProductBundleItemDto> bundleItems = List.of();

    @AssertTrue(message = "Combo products must include bundle items")
    public boolean isComboWithBundleItems() {
        if (Boolean.TRUE.equals(isCombo)) {
            return bundleItems != null && !bundleItems.isEmpty();
        }
        return true;
    }

    @AssertTrue(message = "Only combo products can define bundle items")
    public boolean isBundleItemsOnlyForCombos() {
        if (bundleItems == null || bundleItems.isEmpty()) {
            return true;
        }
        return Boolean.TRUE.equals(isCombo);
    }

    @AssertTrue(message = "Variants are not allowed for combo products")
    public boolean isVariantsOnlyForNonCombos() {
        if (variants == null || variants.isEmpty()) {
            return true;
        }
        return !Boolean.TRUE.equals(isCombo);
    }
}

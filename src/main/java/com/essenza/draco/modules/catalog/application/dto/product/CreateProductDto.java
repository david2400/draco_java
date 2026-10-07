package com.essenza.draco.modules.catalog.application.dto.product;

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
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    /** Contenido neto (500 ml, 1 kg…): valor y unidad juntos; ambos vacíos = sin contenido. */
    @jakarta.validation.constraints.DecimalMin(value = "0", inclusive = false)
    private java.math.BigDecimal netContent;
    private Long netContentUnitId;
    private String imageUrl;
    private Boolean available;

    /**
     * Estado editorial (DRAFT, ACTIVE, INACTIVE, ARCHIVED). Opcional: si falta se
     * deriva de {@code available}. Si se envía, {@code available} se ignora.
     */
    @Pattern(regexp = "DRAFT|ACTIVE|INACTIVE|ARCHIVED", message = "Estado no válido")
    private String status;

    /** Opcional: si falta se genera a partir del nombre. Se normaliza y se garantiza único. */
    @Size(max = 180)
    private String slug;
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

    /**
     * Variantes. {@code null} (campo ausente) en una actualización significa
     * "conservar las actuales"; una lista vacía significa "eliminarlas".
     */
    @Valid
    private List<ProductVariantDto> variants;

    /** Componentes del combo. Misma semántica que {@link #variants}. */
    @Valid
    private List<ProductBundleItemDto> bundleItems;

    @AssertTrue(message = "Combo products must include bundle items")
    public boolean isComboWithBundleItems() {
        if (Boolean.TRUE.equals(isCombo) && bundleItems != null) {
            return !bundleItems.isEmpty();
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

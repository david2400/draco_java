package com.essenza.draco.modules.catalog.application.dto.product_child;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductChildDto {
    @NotNull
    @Positive
    private Long productId;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @PositiveOrZero
    private Integer stock;
    @NotNull
    @Positive
    private BigDecimal unitPrice;
    private String imageUrl;
    /** Contenido neto de la variante (50 ml, 100 ml…): valor y unidad juntos. */
    @jakarta.validation.constraints.DecimalMin(value = "0", inclusive = false)
    private BigDecimal netContent;
    private Long netContentUnitId;
    private Boolean available;
}

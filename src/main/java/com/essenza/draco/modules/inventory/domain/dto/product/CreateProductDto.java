package com.essenza.draco.modules.inventory.domain.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

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

    private Boolean isCombo = false;
}

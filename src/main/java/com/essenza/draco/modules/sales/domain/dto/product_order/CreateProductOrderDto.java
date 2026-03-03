package com.essenza.draco.modules.sales.domain.dto.product_order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductOrderDto {
    @NotNull
    @Positive
    private Integer quantity;
    @NotNull
    @PositiveOrZero
    private BigDecimal discount;
    @NotNull
    @PositiveOrZero
    private BigDecimal subtotal;
    @NotNull
    @PositiveOrZero
    private BigDecimal total;
    @NotNull
    @Positive
    private Long productId;
    @NotNull
    @Positive
    private Long orderId;
}

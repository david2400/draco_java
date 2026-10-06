package com.essenza.draco.modules.sales.application.dto.product_order;

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
    /** Descuento en valor (no porcentaje); 0 si no se envía. */
    @PositiveOrZero
    private BigDecimal discount;
    /** Calculado por el backend; se ignora si se envía. */
    @PositiveOrZero
    private BigDecimal subtotal;
    /** Calculado por el backend; se ignora si se envía. */
    @PositiveOrZero
    private BigDecimal total;
    /** Producto; basta para productos simples o combos (se usa su SKU por defecto). */
    @Positive
    private Long productId;

    /** SKU vendido; obligatorio si el producto tiene variantes. */
    @Positive
    private Long skuId;
    @NotNull
    @Positive
    private Long orderId;
}

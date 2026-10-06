package com.essenza.draco.modules.sales.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Importes de una línea de orden a partir del precio unitario congelado:
 * subtotal = precio × cantidad; total = subtotal − descuento (redondeo a 2 decimales).
 */
public record OrderLineAmounts(BigDecimal unitPrice, int quantity, BigDecimal discount, BigDecimal subtotal, BigDecimal total) {

    public static OrderLineAmounts of(BigDecimal unitPrice, Integer quantity, BigDecimal discount) {
        Objects.requireNonNull(unitPrice, "unitPrice");
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        BigDecimal price = unitPrice.setScale(2, RoundingMode.HALF_UP);
        BigDecimal off = discount == null ? BigDecimal.ZERO.setScale(2) : discount.setScale(2, RoundingMode.HALF_UP);
        if (off.signum() < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo.");
        }
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        if (off.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("El descuento (" + off.toPlainString() + ") supera el subtotal ("
                    + subtotal.toPlainString() + ").");
        }
        return new OrderLineAmounts(price, quantity, off, subtotal, subtotal.subtract(off));
    }
}

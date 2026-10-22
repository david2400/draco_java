package com.essenza.draco.modules.sales.domain.model.order;

/** Descuento global de la orden. */
public enum DiscountType {
    NONE, PERCENT, AMOUNT, COUPON;

    public static DiscountType parse(String value) {
        return Enums.parse(DiscountType.class, value, NONE, "Tipo de descuento no válido: %s (NONE, PERCENT, AMOUNT o COUPON).");
    }
}

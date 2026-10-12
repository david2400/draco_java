package com.essenza.draco.modules.sales.domain.model.payment;

import java.util.Locale;

/** Dónde ocurre el pago: en tienda / entrega (físico) o en línea (virtual). */
public enum PaymentChannel {
    PHYSICAL,
    VIRTUAL;

    public static PaymentChannel parse(String value) {
        if (value == null || value.isBlank()) {
            throw new PaymentRuleViolation("El canal es obligatorio (PHYSICAL o VIRTUAL).");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new PaymentRuleViolation("Canal no válido: " + value + " (PHYSICAL o VIRTUAL).");
        }
    }
}

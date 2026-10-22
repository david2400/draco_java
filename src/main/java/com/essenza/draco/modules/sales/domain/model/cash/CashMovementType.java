package com.essenza.draco.modules.sales.domain.model.cash;

import java.util.Locale;

/** Entrada (IN: base adicional, cambio) o salida (OUT: retiro a bóveda, gasto menor) de efectivo. */
public enum CashMovementType {
    IN,
    OUT;

    public static CashMovementType parse(String value) {
        try {
            return valueOf(value == null ? "" : value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new CashRuleViolation("Tipo de movimiento no válido: " + value + " (IN o OUT).");
        }
    }
}

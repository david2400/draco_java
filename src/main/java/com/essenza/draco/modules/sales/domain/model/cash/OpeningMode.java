package com.essenza.draco.modules.sales.domain.model.cash;

import java.util.Locale;

/** Base de apertura del turno: libre, sugerida (se propone y se puede cambiar) o fija. */
public enum OpeningMode {
    FREE,
    SUGGESTED,
    FIXED;

    public static OpeningMode parse(String value) {
        if (value == null || value.isBlank()) {
            return FREE;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new CashRuleViolation("Modo de base no válido: " + value + " (FREE, SUGGESTED, FIXED).");
        }
    }
}

package com.essenza.draco.modules.sales.domain.model.order;

import java.util.Locale;

final class Enums {

    private Enums() {
    }

    /** Valor del enum (sin distinguir mayúsculas); vacío = {@code fallback}. */
    static <E extends Enum<E>> E parse(Class<E> type, String value, E fallback, String error) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new OrderRuleViolation(String.format(error, value));
        }
    }
}

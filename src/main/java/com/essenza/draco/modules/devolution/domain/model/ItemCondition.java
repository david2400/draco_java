package com.essenza.draco.modules.devolution.domain.model;

import java.util.Locale;

/** Estado en que llega cada producto devuelto; define si vuelve al stock por defecto. */
public enum ItemCondition {
    NEW(true),
    OPENED(true),
    DAMAGED(false),
    DEFECTIVE(false);

    private final boolean restockByDefault;

    ItemCondition(boolean restockByDefault) {
        this.restockByDefault = restockByDefault;
    }

    public boolean restockByDefault() {
        return restockByDefault;
    }

    public static ItemCondition parse(String value) {
        if (value == null || value.isBlank()) {
            throw new DevolutionRuleViolation("Indica el estado en que llegó cada producto.");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new DevolutionRuleViolation("Estado del producto no válido: " + value
                    + " (NEW, OPENED, DAMAGED, DEFECTIVE).");
        }
    }
}

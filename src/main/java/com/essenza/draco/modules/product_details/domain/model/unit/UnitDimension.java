package com.essenza.draco.modules.product_details.domain.model.unit;

import java.util.Locale;

/** Magnitud física de una unidad. Solo se convierte entre unidades de la misma magnitud. */
public enum UnitDimension {
    LENGTH, MASS, VOLUME, AREA, COUNT, OTHER;

    public static UnitDimension parse(String value) {
        if (value == null || value.isBlank()) {
            return OTHER;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new UnitRuleViolation("Magnitud desconocida: " + value
                    + " (usa LENGTH, MASS, VOLUME, AREA, COUNT u OTHER).");
        }
    }
}

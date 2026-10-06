package com.essenza.draco.modules.catalog.domain.model.attribute;

import java.util.Locale;

/** Tipo de dato de un atributo. Solo {@link #OPTION} puede ser eje de variante. */
public enum AttributeDataType {
    TEXT,
    NUMBER,
    BOOLEAN,
    OPTION;

    public static AttributeDataType parse(String value) {
        if (value == null || value.isBlank()) {
            throw new AttributeRuleViolation("El tipo de dato es obligatorio (TEXT, NUMBER, BOOLEAN u OPTION).");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new AttributeRuleViolation("Tipo de dato no válido: " + value + " (TEXT, NUMBER, BOOLEAN u OPTION).");
        }
    }
}

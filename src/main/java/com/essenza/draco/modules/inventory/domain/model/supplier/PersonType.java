package com.essenza.draco.modules.inventory.domain.model.supplier;

public enum PersonType {
    NATURAL, JURIDICA;

    public static PersonType of(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new SupplierRuleViolation("Tipo de persona no válido: " + value + " (NATURAL o JURIDICA).");
        }
    }
}

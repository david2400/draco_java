package com.essenza.draco.modules.inventory.domain.model.supplier;

/** Régimen frente al IVA (Colombia): responsable, no responsable o régimen simple de tributación. */
public enum TaxRegime {
    RESPONSABLE_IVA, NO_RESPONSABLE_IVA, SIMPLE;

    public static TaxRegime of(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new SupplierRuleViolation("Régimen no válido: " + value + " (RESPONSABLE_IVA, NO_RESPONSABLE_IVA o SIMPLE).");
        }
    }
}

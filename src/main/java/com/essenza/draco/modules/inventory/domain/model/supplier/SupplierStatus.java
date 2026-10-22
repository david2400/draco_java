package com.essenza.draco.modules.inventory.domain.model.supplier;

/** ACTIVE: se le compra; BLOCKED: suspendido con motivo (no aparece en selectores); INACTIVE: ya no se usa. */
public enum SupplierStatus {
    ACTIVE, BLOCKED, INACTIVE;

    public static SupplierStatus of(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new SupplierRuleViolation("Estado de proveedor no válido: " + value + " (ACTIVE, BLOCKED o INACTIVE).");
        }
    }
}

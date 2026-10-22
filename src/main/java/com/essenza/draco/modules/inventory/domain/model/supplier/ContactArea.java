package com.essenza.draco.modules.inventory.domain.model.supplier;

/** Área de la persona de contacto en el proveedor. */
public enum ContactArea {
    VENTAS, CARTERA, LOGISTICA, GERENCIA, OTRO;

    public static ContactArea of(String value) {
        if (value == null || value.isBlank()) {
            return VENTAS;
        }
        try {
            return valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new SupplierRuleViolation("Área de contacto no válida: " + value + " (VENTAS, CARTERA, LOGISTICA, GERENCIA u OTRO).");
        }
    }
}

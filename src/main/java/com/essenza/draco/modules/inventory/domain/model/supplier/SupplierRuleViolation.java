package com.essenza.draco.modules.inventory.domain.model.supplier;

/** Regla de la ficha del proveedor incumplida (se responde 400). */
public class SupplierRuleViolation extends IllegalArgumentException {

    public SupplierRuleViolation(String message) {
        super(message);
    }
}

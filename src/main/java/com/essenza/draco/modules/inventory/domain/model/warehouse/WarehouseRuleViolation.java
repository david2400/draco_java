package com.essenza.draco.modules.inventory.domain.model.warehouse;

/** Regla de la bodega incumplida (se responde 400). */
public class WarehouseRuleViolation extends IllegalArgumentException {

    public WarehouseRuleViolation(String message) {
        super(message);
    }
}

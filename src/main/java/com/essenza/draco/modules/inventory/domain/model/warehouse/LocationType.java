package com.essenza.draco.modules.inventory.domain.model.warehouse;

import java.util.Locale;

/** Uso de una ubicación interna. */
public enum LocationType {
    STORAGE, PICKING, RECEIVING, DISPATCH, QUARANTINE;

    public static LocationType of(String value) {
        if (value == null || value.isBlank()) {
            return STORAGE;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new WarehouseRuleViolation("Tipo de ubicación no válido: " + value
                    + " (STORAGE, PICKING, RECEIVING, DISPATCH o QUARANTINE).");
        }
    }
}

package com.essenza.draco.modules.inventory.domain.model.warehouse;

import java.util.Locale;

/**
 * ALMACEN: almacenamiento general o centro de distribución · TIENDA: bodega de un punto de venta ·
 * TRANSITO: mercancía en camino (no vende) · DEVOLUCIONES: recibe lo devuelto · CONSIGNACION:
 * mercancía de terceros.
 */
public enum WarehouseType {
    ALMACEN, TIENDA, TRANSITO, DEVOLUCIONES, CONSIGNACION;

    public static WarehouseType of(String value) {
        if (value == null || value.isBlank()) {
            return ALMACEN;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new WarehouseRuleViolation("Tipo de bodega no válido: " + value
                    + " (ALMACEN, TIENDA, TRANSITO, DEVOLUCIONES o CONSIGNACION).");
        }
    }

    /** Puede ser la bodega principal (recibe los ajustes desde la ficha de producto). */
    public boolean canBeMain() {
        return this == ALMACEN || this == TIENDA;
    }
}

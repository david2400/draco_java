package com.essenza.draco.modules.inventory.domain.model.warehouse;

/**
 * Qué se hace en la bodega: vender (POS y tiendas), despachar pedidos de la tienda en línea,
 * recibir compras, y en qué orden se usa para despachar (1 = primero).
 */
public record WarehouseOperation(boolean sales, boolean online, boolean receiving, int priority) {

    public static final WarehouseOperation ALL = new WarehouseOperation(true, true, true, 10);

    public WarehouseOperation {
        if (priority < 1 || priority > 99) {
            throw new WarehouseRuleViolation("La prioridad de despacho va de 1 (primero) a 99.");
        }
    }
}

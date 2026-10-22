package com.essenza.draco.modules.inventory.domain.model.warehouse;

import java.util.Locale;

/**
 * Ubicación interna de la bodega (zona, pasillo, estante, nivel). Si el código no llega se arma
 * con las partes: A-01-03-2.
 */
public record WarehouseLocation(Long id, Long warehouseId, String code, String zone, String aisle, String rack,
                                String level, LocationType type, Integer capacityUnits, boolean active) {

    public WarehouseLocation {
        if (warehouseId == null) {
            throw new WarehouseRuleViolation("Falta la bodega de la ubicación.");
        }
        zone = up(zone);
        aisle = up(aisle);
        rack = up(rack);
        level = up(level);
        code = up(code);
        if (code == null) {
            code = String.join("-", java.util.stream.Stream.of(zone, aisle, rack, level)
                    .filter(java.util.Objects::nonNull).toList());
        }
        if (code.isEmpty()) {
            throw new WarehouseRuleViolation("Indica el código de la ubicación o al menos la zona.");
        }
        if (!code.matches("[A-Z0-9][A-Z0-9_.-]{0,29}")) {
            throw new WarehouseRuleViolation("El código de ubicación usa letras, números, punto, guion o guion bajo (máx. 30).");
        }
        type = type == null ? LocationType.STORAGE : type;
        if (capacityUnits != null && capacityUnits <= 0) {
            throw new WarehouseRuleViolation("La capacidad de la ubicación debe ser mayor que 0.");
        }
        check(zone, 30, "zona");
        check(aisle, 10, "pasillo");
        check(rack, 10, "estante");
        check(level, 10, "nivel");
    }

    private static String up(String value) {
        return value == null || value.isBlank() ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    private static void check(String value, int max, String label) {
        if (value != null && value.length() > max) {
            throw new WarehouseRuleViolation("El campo " + label + " tiene máximo " + max + " caracteres.");
        }
    }
}

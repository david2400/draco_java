package com.essenza.draco.modules.sales.domain.model.cash;

import java.util.Locale;

/**
 * Caja (punto de cobro) de un punto de venta: código único, nombre, ubicación, bodega propia
 * opcional (si no, la de la tienda) y sus reglas de operación.
 */
public record CashRegister(Long id, String code, String name, String location, Long warehouseId, boolean active,
                           Long storeId, RegisterSettings settings) {

    public CashRegister {
        code = code == null ? null : code.trim().toUpperCase(Locale.ROOT);
        if (code == null || !code.matches("[A-Z][A-Z0-9_]{1,29}")) {
            throw new CashRuleViolation("El código de la caja usa mayúsculas, números y guion bajo (2 a 30 caracteres).");
        }
        if (name == null || name.isBlank()) {
            throw new CashRuleViolation("El nombre de la caja es obligatorio.");
        }
        name = name.trim();
        location = location == null || location.isBlank() ? null : location.trim();
        settings = settings == null ? RegisterSettings.defaults() : settings;
    }

    public CashRegister(Long id, String code, String name, String location, Long warehouseId, boolean active) {
        this(id, code, name, location, warehouseId, active, null, null);
    }
}

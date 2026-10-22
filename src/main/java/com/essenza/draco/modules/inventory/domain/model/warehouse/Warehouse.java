package com.essenza.draco.modules.inventory.domain.model.warehouse;

import java.util.Locale;

/**
 * Bodega: identificación, tipo, operación, ubicación (catálogo de parametros), responsable,
 * horario, capacidad y estado. Una sola bodega es la principal.
 *
 * @param city nombre de la ciudad del catálogo de parametros (para listados)
 */
public record Warehouse(Long id, String code, String name, WarehouseType type, boolean main, boolean active,
                        WarehouseOperation operation, String address, Long countryId, Long stateId, Long cityId,
                        String city, String managerName, String managerPhone, String managerEmail,
                        String openingHours, String notes, WarehouseCapacity capacity) {

    public Warehouse {
        code = code == null ? null : code.trim().toUpperCase(Locale.ROOT);
        if (code == null || !code.matches("[A-Z0-9][A-Z0-9_.-]{0,49}")) {
            throw new WarehouseRuleViolation("El código de la bodega usa letras, números, punto, guion y guion bajo (máx. 50).");
        }
        if (name == null || name.isBlank()) {
            throw new WarehouseRuleViolation("El nombre de la bodega es obligatorio.");
        }
        name = name.trim();
        type = type == null ? WarehouseType.ALMACEN : type;
        operation = operation == null ? WarehouseOperation.ALL : operation;
        capacity = capacity == null ? WarehouseCapacity.NONE : capacity;
        address = blank(address);
        city = blank(city);
        managerName = blank(managerName);
        managerPhone = blank(managerPhone);
        managerEmail = blank(managerEmail);
        openingHours = blank(openingHours);
        notes = blank(notes);

        if (type == WarehouseType.TRANSITO && operation.sales()) {
            throw new WarehouseRuleViolation("Una bodega de tránsito no vende: la mercancía todavía va en camino.");
        }
        if (main && !type.canBeMain()) {
            throw new WarehouseRuleViolation("La bodega principal debe ser de tipo almacén o tienda.");
        }
        if (main && !active) {
            throw new WarehouseRuleViolation("La bodega principal no se puede desactivar: marca otra como principal primero.");
        }
        if (managerEmail != null && !managerEmail.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new WarehouseRuleViolation("El correo del responsable no es válido.");
        }
        if (cityId != null && stateId == null) {
            throw new WarehouseRuleViolation("Elige el departamento de la ciudad.");
        }
        if (stateId != null && countryId == null) {
            throw new WarehouseRuleViolation("Elige el país del departamento.");
        }
    }

    public Warehouse asMain(boolean value) {
        return new Warehouse(id, code, name, type, value, active, operation, address, countryId, stateId, cityId, city,
                managerName, managerPhone, managerEmail, openingHours, notes, capacity);
    }

    public Warehouse withActive(boolean value) {
        return new Warehouse(id, code, name, type, main, value, operation, address, countryId, stateId, cityId, city,
                managerName, managerPhone, managerEmail, openingHours, notes, capacity);
    }

    private static String blank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

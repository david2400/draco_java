package com.essenza.draco.modules.inventory.domain.model.supplier;

/** Persona de contacto del proveedor. */
public record SupplierContact(Long id, String name, String position, ContactArea area, String phone, String email,
                              boolean primary) {

    public SupplierContact {
        if (name == null || name.isBlank()) {
            throw new SupplierRuleViolation("Cada contacto necesita un nombre.");
        }
        name = name.trim();
        position = Supplier.blank(position);
        area = area == null ? ContactArea.VENTAS : area;
        phone = Supplier.blank(phone);
        email = Supplier.blank(email);
        if (phone == null && email == null) {
            throw new SupplierRuleViolation("El contacto " + name + " necesita teléfono o correo.");
        }
        if (email != null && !Supplier.isEmail(email)) {
            throw new SupplierRuleViolation("El correo del contacto " + name + " no es válido.");
        }
    }

    public SupplierContact asPrimary(boolean value) {
        return new SupplierContact(id, name, position, area, phone, email, value);
    }
}

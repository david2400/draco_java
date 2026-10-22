package com.essenza.draco.modules.sales.domain.model.cash;

import java.util.Locale;

/**
 * Punto de venta (tienda o sede): agrupa cajas, define la bodega de la que salen las ventas
 * de mostrador y los textos del ticket.
 */
public record Store(Long id, String code, String name, String address, String city, String phone, String email,
                    Long warehouseId, String openingHours, String ticketHeader, String ticketFooter, boolean active,
                    Long countryId, Long stateId, Long cityId) {

    /** Sin ubicación del catálogo de parámetros (país, departamento, ciudad). */
    public Store(Long id, String code, String name, String address, String city, String phone, String email,
                 Long warehouseId, String openingHours, String ticketHeader, String ticketFooter, boolean active) {
        this(id, code, name, address, city, phone, email, warehouseId, openingHours, ticketHeader, ticketFooter, active,
                null, null, null);
    }

    public Store {
        code = code == null ? null : code.trim().toUpperCase(Locale.ROOT);
        if (code == null || !code.matches("[A-Z][A-Z0-9_]{1,29}")) {
            throw new CashRuleViolation("El código del punto de venta usa mayúsculas, números y guion bajo (2 a 30 caracteres).");
        }
        if (name == null || name.isBlank()) {
            throw new CashRuleViolation("El nombre del punto de venta es obligatorio.");
        }
        name = name.trim();
        address = blank(address);
        city = blank(city);
        phone = blank(phone);
        email = blank(email);
        openingHours = blank(openingHours);
        ticketHeader = blank(ticketHeader);
        ticketFooter = blank(ticketFooter);
        // Ubicación del servicio parametros: la ciudad exige departamento y este exige país.
        if (cityId != null && stateId == null) {
            throw new CashRuleViolation("Elige el departamento de la ciudad.");
        }
        if (stateId != null && countryId == null) {
            throw new CashRuleViolation("Elige el país del departamento.");
        }
        if (email != null && !email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new CashRuleViolation("El correo del punto de venta no es válido.");
        }
    }

    private static String blank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

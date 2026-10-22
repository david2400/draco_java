package com.essenza.draco.modules.inventory.domain.model.supplier;

import java.util.Locale;
import java.util.Set;

/**
 * Documento del proveedor: tipo, número normalizado y dígito de verificación (NIT, módulo 11 de la DIAN).
 * Mismo cálculo que billing ({@code Nit}); se repite aquí para no acoplar inventario con facturación.
 */
public record TaxId(String type, String number, String dv) {

    public static final Set<String> TYPES = Set.of("NIT", "CC", "CE", "PP", "PEP", "NIT_EXT");
    private static final int[] WEIGHTS = {3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71};

    /** null si no llega documento (proveedores anteriores a V13). */
    public static TaxId of(String type, String number) {
        boolean noType = type == null || type.isBlank();
        boolean noNumber = number == null || number.isBlank();
        if (noType && noNumber) {
            return null;
        }
        if (noType || noNumber) {
            throw new SupplierRuleViolation("Indica el tipo y el número de documento del proveedor.");
        }
        String t = type.trim().toUpperCase(Locale.ROOT);
        if (!TYPES.contains(t)) {
            throw new SupplierRuleViolation("Tipo de documento no válido: " + type + " (NIT, CC, CE, PP, PEP o NIT_EXT).");
        }
        String n = switch (t) {
            case "NIT", "CC" -> number.replaceAll("[^0-9]", "");
            default -> number.trim().toUpperCase(Locale.ROOT).replaceAll("\\s+", "");
        };
        if ("NIT".equals(t) || "CC".equals(t)) {
            if (n.length() < 5 || n.length() > 15) {
                throw new SupplierRuleViolation("El " + t + " debe tener entre 5 y 15 dígitos (sin el dígito de verificación).");
            }
        } else if (!n.matches("[A-Z0-9-]{3,20}")) {
            throw new SupplierRuleViolation("El número de documento usa letras, números y guion (3 a 20 caracteres).");
        }
        return new TaxId(t, n, "NIT".equals(t) ? checkDigit(n) : null);
    }

    public static String checkDigit(String digits) {
        int sum = 0;
        for (int i = 0; i < digits.length(); i++) {
            sum += (digits.charAt(digits.length() - 1 - i) - '0') * WEIGHTS[i];
        }
        int remainder = sum % 11;
        return String.valueOf(remainder > 1 ? 11 - remainder : remainder);
    }

    /** NIT con DV para mostrar: 900123456-8. */
    public String formatted() {
        return dv == null ? number : number + "-" + dv;
    }
}

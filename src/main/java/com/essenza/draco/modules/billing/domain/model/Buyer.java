package com.essenza.draco.modules.billing.domain.model;

import java.util.Locale;
import java.util.Set;

/**
 * Adquiriente de la factura. Sin datos se factura a "Consumidor final" (CC 222222222222,
 * como indica la DIAN). Con NIT se calcula el dígito de verificación.
 */
public record Buyer(String docType, String docNumber, String dv, String name, String email, String phone,
                    String address, String city) {

    public static final String FINAL_CONSUMER_NUMBER = "222222222222";
    public static final Set<String> DOC_TYPES = Set.of("CC", "NIT", "CE", "PP", "TI", "PEP", "NIT_EXT");

    public Buyer {
        docType = docType == null || docType.isBlank() ? "CC" : docType.trim().toUpperCase(Locale.ROOT);
        if (!DOC_TYPES.contains(docType)) {
            throw new BillingRuleViolation("Tipo de documento no válido: " + docType + " (CC, NIT, CE, PP, TI, PEP o NIT_EXT).");
        }
        docNumber = docNumber == null ? "" : docNumber.trim();
        if ("NIT".equals(docType) || "CC".equals(docType) || "TI".equals(docType)) {
            docNumber = Nit.digits(docNumber);
        }
        if (docNumber.isEmpty() || docNumber.length() > 20) {
            throw new BillingRuleViolation("Indica el número de documento del comprador.");
        }
        dv = "NIT".equals(docType) ? Nit.checkDigit(docNumber) : null;
        if (name == null || name.isBlank()) {
            throw new BillingRuleViolation("Indica el nombre o razón social del comprador.");
        }
        name = name.trim();
        email = blankToNull(email);
        if (email != null && !email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new BillingRuleViolation("Correo del comprador no válido: " + email);
        }
        phone = blankToNull(phone);
        address = blankToNull(address);
        city = blankToNull(city);
    }

    public static Buyer finalConsumer() {
        return new Buyer("CC", FINAL_CONSUMER_NUMBER, null, "Consumidor final", null, null, null, null);
    }

    public boolean isFinalConsumer() {
        return FINAL_CONSUMER_NUMBER.equals(docNumber);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

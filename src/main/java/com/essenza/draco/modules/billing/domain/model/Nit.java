package com.essenza.draco.modules.billing.domain.model;

/** Dígito de verificación del NIT (módulo 11 de la DIAN). */
public final class Nit {

    private static final int[] WEIGHTS = {3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71};

    private Nit() {
    }

    /** Solo dígitos, sin puntos ni guion (el DV se recibe aparte). */
    public static String digits(String value) {
        return value == null ? "" : value.replaceAll("[^0-9]", "");
    }

    public static String checkDigit(String nit) {
        String digits = digits(nit);
        if (digits.isEmpty() || digits.length() > WEIGHTS.length) {
            throw new BillingRuleViolation("NIT no válido: " + nit);
        }
        int sum = 0;
        for (int i = 0; i < digits.length(); i++) {
            int digit = digits.charAt(digits.length() - 1 - i) - '0';
            sum += digit * WEIGHTS[i];
        }
        int remainder = sum % 11;
        return String.valueOf(remainder > 1 ? 11 - remainder : remainder);
    }
}

package com.essenza.draco.modules.billing.domain.model;

import java.util.Locale;

/** Ambiente de la DIAN: pruebas (habilitación) o producción. Código del CUFE: 2 pruebas, 1 producción. */
public enum BillingEnvironment {
    TEST("2"),
    PRODUCTION("1");

    private final String dianCode;

    BillingEnvironment(String dianCode) {
        this.dianCode = dianCode;
    }

    public String dianCode() {
        return dianCode;
    }

    public static BillingEnvironment parse(String value) {
        if (value == null || value.isBlank()) {
            return TEST;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BillingRuleViolation("Ambiente no válido: " + value + " (TEST o PRODUCTION).");
        }
    }
}

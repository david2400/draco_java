package com.essenza.draco.modules.billing.domain.model;

import java.util.Locale;
import java.util.Optional;

/** Tipo de impuesto con su código DIAN (01 IVA, 04 INC; excluido no se reporta). */
public enum TaxKind {
    IVA("01"),
    INC("04"),
    EXCLUDED(null);

    private final String dianCode;

    TaxKind(String dianCode) {
        this.dianCode = dianCode;
    }

    public String dianCode() {
        return dianCode;
    }

    public static Optional<TaxKind> tryParse(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(value.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public static TaxKind parse(String value) {
        return tryParse(value).orElseThrow(() -> new BillingRuleViolation("Tipo de impuesto no válido: " + value
                + " (use IVA, INC o EXCLUDED)."));
    }
}

package com.essenza.draco.modules.sales.domain.model.payment;

import java.util.Locale;
import java.util.Optional;

/** Pasarelas de pago en línea soportadas. */
public enum GatewayProvider {
    /** Simulador interno para probar el flujo completo sin dinero real. */
    SANDBOX,
    WOMPI,
    MERCADO_PAGO;

    public static Optional<GatewayProvider> tryParse(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(value.trim().toUpperCase(Locale.ROOT).replace('-', '_')));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public static GatewayProvider parse(String value) {
        return tryParse(value).orElseThrow(() -> new PaymentRuleViolation(
                "Pasarela no válida: " + value + " (SANDBOX, WOMPI o MERCADO_PAGO)."));
    }
}

package com.essenza.draco.modules.sales.domain.model.payment;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Estado de un pago.
 * <pre>
 * PENDING  → APPROVED | DECLINED | EXPIRED | VOIDED
 * APPROVED → VOIDED | REFUNDED (reembolso total; el parcial deja APPROVED con refunded &gt; 0)
 * EXPIRED  → APPROVED (la pasarela confirma tarde un link vencido o reemplazado: el dinero sí llegó)
 * </pre>
 */
public enum PaymentStatus {
    PENDING,
    APPROVED,
    DECLINED,
    EXPIRED,
    VOIDED,
    REFUNDED;

    public Set<PaymentStatus> next() {
        return switch (this) {
            case PENDING -> EnumSet.of(APPROVED, DECLINED, EXPIRED, VOIDED);
            case APPROVED -> EnumSet.of(VOIDED, REFUNDED);
            case EXPIRED -> EnumSet.of(APPROVED);
            case DECLINED, VOIDED, REFUNDED -> EnumSet.noneOf(PaymentStatus.class);
        };
    }

    public boolean canTransitionTo(PaymentStatus target) {
        return next().contains(target);
    }

    public boolean isFinal() {
        return next().isEmpty();
    }

    public static Optional<PaymentStatus> tryParse(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(value.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public static PaymentStatus parse(String value) {
        return tryParse(value).orElseThrow(() -> new PaymentRuleViolation("Estado de pago no válido: " + value + "."));
    }
}

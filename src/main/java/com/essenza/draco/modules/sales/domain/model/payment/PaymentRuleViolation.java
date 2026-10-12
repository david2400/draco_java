package com.essenza.draco.modules.sales.domain.model.payment;

/** Regla de medios de pago o de pagos incumplida (se responde 400). */
public class PaymentRuleViolation extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public PaymentRuleViolation(String message) {
        super(message);
    }
}

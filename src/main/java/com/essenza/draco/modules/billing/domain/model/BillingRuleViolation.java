package com.essenza.draco.modules.billing.domain.model;

/** Regla fiscal incumplida (se responde 400 con el mensaje). */
public class BillingRuleViolation extends IllegalArgumentException {

    public BillingRuleViolation(String message) {
        super(message);
    }
}

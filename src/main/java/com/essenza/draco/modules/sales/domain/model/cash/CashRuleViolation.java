package com.essenza.draco.modules.sales.domain.model.cash;

/** Regla de caja incumplida (400). */
public class CashRuleViolation extends IllegalArgumentException {

    public CashRuleViolation(String message) {
        super(message);
    }
}

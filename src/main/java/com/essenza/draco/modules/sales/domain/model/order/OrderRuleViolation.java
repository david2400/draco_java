package com.essenza.draco.modules.sales.domain.model.order;

/** Regla de orden incumplida (400). */
public class OrderRuleViolation extends IllegalArgumentException {

    public OrderRuleViolation(String message) {
        super(message);
    }
}

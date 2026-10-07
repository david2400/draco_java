package com.essenza.draco.modules.product_details.domain.model.unit;

/** Regla de unidades incumplida por los datos enviados (400). */
public class UnitRuleViolation extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public UnitRuleViolation(String message) {
        super(message);
    }
}

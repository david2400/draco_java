package com.essenza.draco.modules.catalog.domain.model.variant;

/** Regla del generador de variantes incumplida (se responde 400). */
public class VariantRuleViolation extends IllegalArgumentException {

    public VariantRuleViolation(String message) {
        super(message);
    }
}

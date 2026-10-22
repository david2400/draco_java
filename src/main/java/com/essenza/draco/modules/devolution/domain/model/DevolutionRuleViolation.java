package com.essenza.draco.modules.devolution.domain.model;

/** Regla del flujo de devoluciones incumplida (400). */
public class DevolutionRuleViolation extends IllegalArgumentException {

    public DevolutionRuleViolation(String message) {
        super(message);
    }
}

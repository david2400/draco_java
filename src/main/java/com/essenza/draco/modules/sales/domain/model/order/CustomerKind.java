package com.essenza.draco.modules.sales.domain.model.order;

/** Persona natural o empresa. */
public enum CustomerKind {
    PERSON, COMPANY;

    public static CustomerKind parse(String value) {
        return Enums.parse(CustomerKind.class, value, PERSON, "Tipo de cliente no válido: %s (PERSON o COMPANY).");
    }
}

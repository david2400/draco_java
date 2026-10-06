package com.essenza.draco.modules.catalog.domain.model.attribute;

/** Valor permitido de un atributo {@link AttributeDataType#OPTION} (p. ej. "Rojo"). */
public record AttributeOption(Long id, String value, int position) {

    public AttributeOption {
        value = value == null ? null : value.trim();
        if (value == null || value.isEmpty()) {
            throw new AttributeRuleViolation("Las opciones no pueden estar vacías.");
        }
        if (value.length() > 120) {
            throw new AttributeRuleViolation("La opción \"" + value.substring(0, 20) + "…\" supera 120 caracteres.");
        }
    }
}

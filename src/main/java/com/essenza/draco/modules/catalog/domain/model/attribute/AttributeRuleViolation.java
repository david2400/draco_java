package com.essenza.draco.modules.catalog.domain.model.attribute;

/**
 * Una regla de atributos o plantillas no se cumple. Extiende
 * {@link IllegalArgumentException}: el manejador global responde 400 con el mensaje.
 */
public class AttributeRuleViolation extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public AttributeRuleViolation(String message) {
        super(message);
    }
}

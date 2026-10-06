package com.essenza.draco.modules.catalog.domain.model.attribute;

import java.math.BigDecimal;
import java.util.Objects;

/** Valor tipado de un atributo en la ficha de un producto. Solo uno de los campos se usa. */
public record AttributeValue(Long attributeId, String text, BigDecimal number, Boolean bool, Long optionId) {

    public AttributeValue {
        Objects.requireNonNull(attributeId, "attributeId");
        text = text == null || text.isBlank() ? null : text.trim();
    }

    public boolean isEmpty() {
        return text == null && number == null && bool == null && optionId == null;
    }

    /** Deja solo el campo que corresponde al tipo del atributo; falla si el valor no encaja. */
    public AttributeValue normalizedFor(Attribute definition) {
        String label = "\"" + definition.getName() + "\"";
        return switch (definition.getDataType()) {
            case TEXT -> {
                if (text == null) {
                    throw new AttributeRuleViolation(label + " espera un texto.");
                }
                if (text.length() > 500) {
                    throw new AttributeRuleViolation(label + " supera 500 caracteres.");
                }
                yield new AttributeValue(attributeId, text, null, null, null);
            }
            case NUMBER -> {
                if (number == null) {
                    throw new AttributeRuleViolation(label + " espera un número.");
                }
                if (number.abs().compareTo(new BigDecimal("99999999999999")) > 0 || number.scale() > 4) {
                    throw new AttributeRuleViolation(label + ": número fuera de rango (máximo 4 decimales).");
                }
                yield new AttributeValue(attributeId, null, number, null, null);
            }
            case BOOLEAN -> {
                if (bool == null) {
                    throw new AttributeRuleViolation(label + " espera sí o no.");
                }
                yield new AttributeValue(attributeId, null, null, bool, null);
            }
            case OPTION -> {
                if (optionId == null || definition.option(optionId).isEmpty()) {
                    throw new AttributeRuleViolation(label + ": la opción elegida no pertenece al atributo.");
                }
                yield new AttributeValue(attributeId, null, null, null, optionId);
            }
        };
    }
}

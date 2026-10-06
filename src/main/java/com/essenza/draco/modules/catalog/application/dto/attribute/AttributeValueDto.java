package com.essenza.draco.modules.catalog.application.dto.attribute;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

/** Valor de la ficha técnica: se usa el campo que corresponde al tipo del atributo. */
public record AttributeValueDto(
        @NotNull Long attributeId,
        String valueText,
        BigDecimal valueNumber,
        Boolean valueBoolean,
        Long optionId) {
}

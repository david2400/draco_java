package com.essenza.draco.modules.catalog.application.dto.attribute;

import java.util.List;

import jakarta.validation.Valid;

/**
 * Reemplaza plantilla, ficha y ejes de variante del producto. {@code template_id}
 * nulo quita la plantilla; listas nulas cuentan como vacías.
 */
public record SaveProductAttributesDto(
        Long templateId,
        @Valid List<AttributeValueDto> values,
        @Valid List<VariantAttributesDto> variants) {
}

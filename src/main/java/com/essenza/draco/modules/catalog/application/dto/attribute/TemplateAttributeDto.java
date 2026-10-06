package com.essenza.draco.modules.catalog.application.dto.attribute;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/** Atributo de una plantilla. {@code attribute} solo viene en las respuestas. */
public record TemplateAttributeDto(
        @NotNull Long attributeId,
        @Schema(description = "Obligatorio para publicar (por defecto false)") Boolean required,
        @Schema(description = "Eje de variante (por defecto false; solo OPTION)") Boolean variantAxis,
        @Schema(description = "Filtrable en la tienda (por defecto false)") Boolean filterable,
        Integer position,
        @Schema(description = "Definición del atributo (solo lectura)", accessMode = Schema.AccessMode.READ_ONLY)
        AttributeDto attribute) {
}

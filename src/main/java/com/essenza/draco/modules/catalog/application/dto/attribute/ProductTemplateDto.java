package com.essenza.draco.modules.catalog.application.dto.attribute;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Plantilla de producto con sus atributos. */
public record ProductTemplateDto(
        Long id,
        String name,
        String description,
        List<TemplateAttributeDto> attributes,
        @Schema(description = "Productos que usan la plantilla (solo lectura)") long productCount) {
}

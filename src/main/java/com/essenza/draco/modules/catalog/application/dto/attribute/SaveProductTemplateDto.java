package com.essenza.draco.modules.catalog.application.dto.attribute;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Alta o edición de una plantilla (PUT reemplaza la lista de atributos). */
public record SaveProductTemplateDto(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 500) String description,
        @Valid List<TemplateAttributeDto> attributes) {
}

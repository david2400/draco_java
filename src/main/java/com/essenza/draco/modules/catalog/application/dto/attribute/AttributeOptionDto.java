package com.essenza.draco.modules.catalog.application.dto.attribute;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Opción de un atributo OPTION. Sin {@code id} se crea; con {@code id} se actualiza. */
public record AttributeOptionDto(
        Long id,
        @NotBlank @Size(max = 120) String value,
        Integer position) {
}

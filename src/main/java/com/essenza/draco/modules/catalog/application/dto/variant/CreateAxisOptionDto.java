package com.essenza.draco.modules.catalog.application.dto.variant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Valor nuevo para un eje (p. ej. un color); si ya existe con el mismo texto se devuelve ese. */
public record CreateAxisOptionDto(@NotNull Long attributeId, @NotBlank @Size(max = 120) String value) {
}

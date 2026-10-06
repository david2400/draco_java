package com.essenza.draco.modules.catalog.application.dto.attribute;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

/** Alta o edición de un atributo (PUT reemplaza: las opciones ausentes se eliminan si no están en uso). */
public record SaveAttributeDto(
        @NotBlank @Pattern(regexp = "^[a-z][a-z0-9_]{1,59}$") String code,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 500) String description,
        @NotNull @Schema(allowableValues = {"TEXT", "NUMBER", "BOOLEAN", "OPTION"}) String dataType,
        Long unitId,
        @Valid List<AttributeOptionDto> options) {
}

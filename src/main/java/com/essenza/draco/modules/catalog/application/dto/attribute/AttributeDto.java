package com.essenza.draco.modules.catalog.application.dto.attribute;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Atributo del catálogo. */
public record AttributeDto(
        Long id,
        String code,
        String name,
        String description,
        @Schema(allowableValues = {"TEXT", "NUMBER", "BOOLEAN", "OPTION"}) String dataType,
        Long unitId,
        @Schema(description = "Nombre de la unidad (solo lectura)") String unitName,
        List<AttributeOptionDto> options,
        @Schema(description = "Se usa en plantillas o valores: no se puede borrar ni cambiar de tipo (solo lectura)")
        boolean inUse) {
}

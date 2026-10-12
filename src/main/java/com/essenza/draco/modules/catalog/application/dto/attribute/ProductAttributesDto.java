package com.essenza.draco.modules.catalog.application.dto.attribute;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Ficha técnica y ejes de variante de un producto. */
public record ProductAttributesDto(
        Long productId,
        String productStatus,
        Long templateId,
        ProductTemplateDto template,
        @Schema(description = "Con plantilla: sus atributos y los adicionales con valor. Sin plantilla: todos") List<AttributeDto> attributes,
        List<AttributeValueDto> values,
        List<VariantAttributesDto> variants,
        @Schema(description = "Lo que falta para poder publicar (vacío si está completo)") List<String> missing) {
}

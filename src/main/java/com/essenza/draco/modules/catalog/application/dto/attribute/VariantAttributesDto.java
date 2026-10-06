package com.essenza.draco.modules.catalog.application.dto.attribute;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/** Ejes de variante de un SKU. {@code sku_code}, {@code name} y {@code active} solo vienen en las respuestas. */
public record VariantAttributesDto(
        @NotNull Long skuId,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String skuCode,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String name,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) Boolean active,
        @Valid List<VariantOptionDto> options) {
}

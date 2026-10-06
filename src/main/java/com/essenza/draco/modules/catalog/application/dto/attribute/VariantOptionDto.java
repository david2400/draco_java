package com.essenza.draco.modules.catalog.application.dto.attribute;

import jakarta.validation.constraints.NotNull;

/** Opción elegida para un eje de variante. */
public record VariantOptionDto(@NotNull Long attributeId, @NotNull Long optionId) {
}

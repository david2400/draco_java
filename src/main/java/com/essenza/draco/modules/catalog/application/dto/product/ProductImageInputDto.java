package com.essenza.draco.modules.catalog.application.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Imagen de la galería general del producto (no ligada a una variante). */
public record ProductImageInputDto(
        @NotBlank @Size(max = 500) String url,
        @Size(max = 255) String altText) {
}

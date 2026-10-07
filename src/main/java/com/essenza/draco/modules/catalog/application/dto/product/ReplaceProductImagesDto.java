package com.essenza.draco.modules.catalog.application.dto.product;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Galería general completa del producto, en orden. La primera es la imagen principal
 * ({@code products.image_url}); una lista vacía quita todas. Las imágenes de cada
 * variante se gestionan desde la variante.
 */
public record ReplaceProductImagesDto(
        @NotNull @Size(max = 30) List<@Valid @NotNull ProductImageInputDto> images) {
}

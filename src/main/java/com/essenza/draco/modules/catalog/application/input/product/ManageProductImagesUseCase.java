package com.essenza.draco.modules.catalog.application.input.product;

import java.util.List;

import com.essenza.draco.modules.catalog.application.dto.product.ProductImageDto;
import com.essenza.draco.modules.catalog.application.dto.product.ReplaceProductImagesDto;

/** Galería de imágenes del producto. */
public interface ManageProductImagesUseCase {

    /** Todas las imágenes (galería general primero, luego las de variantes), en orden. */
    List<ProductImageDto> list(Long productId);

    /** Sustituye la galería general; devuelve el resultado como {@link #list(Long)}. */
    List<ProductImageDto> replace(Long productId, ReplaceProductImagesDto input);
}

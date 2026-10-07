package com.essenza.draco.modules.catalog.application.output.repository;

import java.util.List;

import com.essenza.draco.modules.catalog.application.dto.product.ProductImageDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductImageInputDto;

/** Persistencia de la galería del producto ({@code product_images}). */
public interface ProductGalleryRepository {

    boolean productExists(Long productId);

    List<ProductImageDto> images(Long productId);

    /** Reemplaza las imágenes sin SKU y alinea {@code products.image_url} con la primera. */
    void replaceGallery(Long productId, List<ProductImageInputDto> images);
}

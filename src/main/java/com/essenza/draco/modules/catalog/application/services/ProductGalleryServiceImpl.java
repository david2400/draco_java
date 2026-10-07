package com.essenza.draco.modules.catalog.application.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.catalog.application.dto.product.ProductImageDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductImageInputDto;
import com.essenza.draco.modules.catalog.application.dto.product.ReplaceProductImagesDto;
import com.essenza.draco.modules.catalog.application.input.product.ManageProductImagesUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductGalleryRepository;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
public class ProductGalleryServiceImpl implements ManageProductImagesUseCase {

    private final ProductGalleryRepository gallery;

    public ProductGalleryServiceImpl(ProductGalleryRepository gallery) {
        this.gallery = gallery;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageDto> list(Long productId) {
        requireProduct(productId);
        return gallery.images(productId);
    }

    @Override
    @Transactional
    public List<ProductImageDto> replace(Long productId, ReplaceProductImagesDto input) {
        requireProduct(productId);
        // URL repetida: se conserva la primera aparición (y su texto alternativo).
        Map<String, ProductImageInputDto> unique = new LinkedHashMap<>();
        for (ProductImageInputDto image : input.images()) {
            String url = image.url().trim();
            String alt = image.altText() == null || image.altText().isBlank() ? null : image.altText().trim();
            unique.putIfAbsent(url, new ProductImageInputDto(url, alt));
        }
        gallery.replaceGallery(productId, List.copyOf(unique.values()));
        return gallery.images(productId);
    }

    private void requireProduct(Long productId) {
        if (!gallery.productExists(productId)) {
            throw new NotFoundException("Producto no encontrado: " + productId);
        }
    }
}

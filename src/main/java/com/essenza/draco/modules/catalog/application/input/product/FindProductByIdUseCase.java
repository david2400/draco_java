package com.essenza.draco.modules.catalog.application.input.product;

import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;

import java.util.Optional;

public interface FindProductByIdUseCase {
    Optional<ProductDto> findById(Long id);
}

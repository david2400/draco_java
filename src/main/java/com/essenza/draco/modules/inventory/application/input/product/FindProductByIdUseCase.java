package com.essenza.draco.modules.inventory.application.input.product;

import com.essenza.draco.modules.inventory.domain.dto.product.ProductDto;

import java.util.Optional;

public interface FindProductByIdUseCase {
    Optional<ProductDto> findById(Long id);
}

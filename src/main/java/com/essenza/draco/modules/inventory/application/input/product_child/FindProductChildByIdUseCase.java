package com.essenza.draco.modules.inventory.application.input.product_child;

import com.essenza.draco.modules.inventory.domain.dto.product_child.ProductChildDto;

import java.util.Optional;

public interface FindProductChildByIdUseCase {
    Optional<ProductChildDto> findById(Long id);
}

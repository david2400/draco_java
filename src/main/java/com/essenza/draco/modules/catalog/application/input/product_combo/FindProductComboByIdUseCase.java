package com.essenza.draco.modules.catalog.application.input.product_combo;

import com.essenza.draco.modules.catalog.application.dto.product_combo.ProductComboDto;

import java.util.Optional;

public interface FindProductComboByIdUseCase {
    Optional<ProductComboDto> findById(Long id);
}

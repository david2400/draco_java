package com.essenza.draco.modules.inventory.application.input.product_combo;

import com.essenza.draco.modules.inventory.domain.dto.product_combo.ProductComboDto;

import java.util.List;

public interface FindProductCombosUseCase {
    List<ProductComboDto> findAll();
}

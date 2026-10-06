package com.essenza.draco.modules.catalog.application.input.product_combo;

import com.essenza.draco.modules.catalog.application.dto.product_combo.ProductComboDto;

import java.util.List;

public interface FindProductCombosUseCase {
    List<ProductComboDto> findAll();
}

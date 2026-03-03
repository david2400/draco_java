package com.essenza.draco.modules.inventory.application.input.product_combo;

import com.essenza.draco.modules.inventory.domain.dto.product_combo.CreateProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.ProductComboDto;

public interface CreateProductComboUseCase {
    ProductComboDto create(CreateProductComboDto input);
}

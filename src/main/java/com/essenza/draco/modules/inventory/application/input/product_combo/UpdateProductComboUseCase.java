package com.essenza.draco.modules.inventory.application.input.product_combo;

import com.essenza.draco.modules.inventory.domain.dto.product_combo.UpdateProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.ProductComboDto;

public interface UpdateProductComboUseCase {
    ProductComboDto update(Long id, UpdateProductComboDto input);
}

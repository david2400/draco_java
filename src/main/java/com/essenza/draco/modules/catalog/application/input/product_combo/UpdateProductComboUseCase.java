package com.essenza.draco.modules.catalog.application.input.product_combo;

import com.essenza.draco.modules.catalog.application.dto.product_combo.UpdateProductComboDto;
import com.essenza.draco.modules.catalog.application.dto.product_combo.ProductComboDto;

public interface UpdateProductComboUseCase {
    ProductComboDto update(Long id, UpdateProductComboDto input);
}

package com.essenza.draco.modules.catalog.application.input.product_combo;

import com.essenza.draco.modules.catalog.application.dto.product_combo.CreateProductComboDto;
import com.essenza.draco.modules.catalog.application.dto.product_combo.ProductComboDto;

public interface CreateProductComboUseCase {
    ProductComboDto create(CreateProductComboDto input);
}

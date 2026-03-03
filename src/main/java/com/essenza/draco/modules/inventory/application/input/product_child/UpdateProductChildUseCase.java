package com.essenza.draco.modules.inventory.application.input.product_child;

import com.essenza.draco.modules.inventory.domain.dto.product_child.UpdateProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.ProductChildDto;

public interface UpdateProductChildUseCase {
    ProductChildDto update(Long id, UpdateProductChildDto input);
}

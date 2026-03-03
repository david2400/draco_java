package com.essenza.draco.modules.inventory.application.input.product_child;

import com.essenza.draco.modules.inventory.domain.dto.product_child.CreateProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.ProductChildDto;

public interface CreateProductChildUseCase {
    ProductChildDto create(CreateProductChildDto input);
}

package com.essenza.draco.modules.catalog.application.input.product_child;

import com.essenza.draco.modules.catalog.application.dto.product_child.CreateProductChildDto;
import com.essenza.draco.modules.catalog.application.dto.product_child.ProductChildDto;

public interface CreateProductChildUseCase {
    ProductChildDto create(CreateProductChildDto input);
}

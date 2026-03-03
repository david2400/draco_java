package com.essenza.draco.modules.inventory.application.input.product;

import com.essenza.draco.modules.inventory.domain.dto.product.CreateProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductDto;

public interface CreateProductUseCase {
    ProductDto create(CreateProductDto input);
}

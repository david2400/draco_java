package com.essenza.draco.modules.catalog.application.input.product;

import com.essenza.draco.modules.catalog.application.dto.product.CreateProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;

public interface CreateProductUseCase {
    ProductDto create(CreateProductDto input);
}

package com.essenza.draco.modules.catalog.application.input.product;

import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.UpdateProductDto;

public interface UpdateProductUseCase {
    ProductDto update(Long id, UpdateProductDto input);
}

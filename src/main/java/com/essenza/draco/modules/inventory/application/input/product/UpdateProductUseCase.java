package com.essenza.draco.modules.inventory.application.input.product;

import com.essenza.draco.modules.inventory.domain.dto.product.ProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.UpdateProductDto;

public interface UpdateProductUseCase {
    ProductDto update(Long id, UpdateProductDto input);
}

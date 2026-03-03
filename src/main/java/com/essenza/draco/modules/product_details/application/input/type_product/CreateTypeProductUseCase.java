package com.essenza.draco.modules.product_details.application.input.type_product;

import com.essenza.draco.modules.product_details.domain.dto.type_product.CreateTypeProductDto;
import com.essenza.draco.modules.product_details.domain.dto.type_product.TypeProductDto;

public interface CreateTypeProductUseCase {
    TypeProductDto create(CreateTypeProductDto input);
}

package com.essenza.draco.modules.product_details.application.input.type_product;

import com.essenza.draco.modules.product_details.domain.dto.type_product.TypeProductDto;

import java.util.Optional;

public interface FindTypeProductByIdUseCase {
    Optional<TypeProductDto> findById(Long id);
}

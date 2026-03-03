package com.essenza.draco.modules.sales.application.input.product_order;

import com.essenza.draco.modules.sales.domain.dto.product_order.ProductOrderDto;

import java.util.Optional;

public interface FindProductOrderByIdUseCase {
    Optional<ProductOrderDto> findById(Long id);
}

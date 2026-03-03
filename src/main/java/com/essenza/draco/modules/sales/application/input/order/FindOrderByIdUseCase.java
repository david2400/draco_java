package com.essenza.draco.modules.sales.application.input.order;

import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;

import java.util.Optional;

public interface FindOrderByIdUseCase {
    Optional<OrderDto> findById(Long id);
}

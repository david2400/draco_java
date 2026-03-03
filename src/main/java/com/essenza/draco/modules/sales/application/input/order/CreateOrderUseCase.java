package com.essenza.draco.modules.sales.application.input.order;

import com.essenza.draco.modules.sales.domain.dto.order.CreateOrderDto;
import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;

public interface CreateOrderUseCase {
    OrderDto create(CreateOrderDto input);
}

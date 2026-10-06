package com.essenza.draco.modules.sales.application.input.order;

import com.essenza.draco.modules.sales.application.dto.order.CreateOrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;

public interface CreateOrderUseCase {
    OrderDto create(CreateOrderDto input);
}

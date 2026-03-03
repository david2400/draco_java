package com.essenza.draco.modules.sales.application.input.order;

import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;
import com.essenza.draco.modules.sales.domain.dto.order.UpdateOrderDto;

public interface UpdateOrderUseCase {
    OrderDto update(Long id, UpdateOrderDto input);
}

package com.essenza.draco.modules.sales.application.input.order;

import com.essenza.draco.modules.sales.application.dto.order.CreateFullOrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;

/** Crea una orden completa (cabecera y productos) en un paso. */
public interface CreateFullOrderUseCase {
    OrderDto createFull(CreateFullOrderDto input);
}

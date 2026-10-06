package com.essenza.draco.modules.sales.application.input.order;

import com.essenza.draco.modules.sales.application.dto.order.ChangeOrderStatusDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;

/** Cambia el estado de una orden y aplica sus efectos en el stock. */
public interface ChangeOrderStatusUseCase {

    OrderDto changeStatus(Long id, ChangeOrderStatusDto input);
}

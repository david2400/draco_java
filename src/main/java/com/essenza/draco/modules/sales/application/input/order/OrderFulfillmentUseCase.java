package com.essenza.draco.modules.sales.application.input.order;

import com.essenza.draco.modules.sales.application.dto.order.OrderActionDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.PickOrderDto;
import com.essenza.draco.modules.sales.application.dto.order.ShipOrderDto;

/** Preparación, despacho y entrega. */
public interface OrderFulfillmentUseCase {

    OrderDto pick(Long orderId, PickOrderDto input);

    /** Prepara todas las líneas de una vez. */
    OrderDto pickAll(Long orderId, OrderActionDto input);

    OrderDto ready(Long orderId, OrderActionDto input);

    OrderDto ship(Long orderId, ShipOrderDto input);

    OrderDto deliver(Long orderId, OrderActionDto input);
}

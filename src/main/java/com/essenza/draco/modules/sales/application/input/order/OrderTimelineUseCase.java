package com.essenza.draco.modules.sales.application.input.order;

import java.util.List;

import com.essenza.draco.modules.sales.application.dto.order.OrderActionDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderEventDto;

/** Línea de tiempo y notas de la orden. */
public interface OrderTimelineUseCase {

    List<OrderEventDto> timeline(Long orderId);

    OrderEventDto addNote(Long orderId, OrderActionDto input);
}

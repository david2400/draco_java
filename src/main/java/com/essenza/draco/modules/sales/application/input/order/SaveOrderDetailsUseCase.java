package com.essenza.draco.modules.sales.application.input.order;

import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.SaveOrderDetailsDto;

/** Edita cliente, entrega, origen, descuento y notas de una orden. */
public interface SaveOrderDetailsUseCase {
    OrderDto saveDetails(Long orderId, SaveOrderDetailsDto input);
}

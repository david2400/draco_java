package com.essenza.draco.modules.sales.application.input.order;

import com.essenza.draco.modules.sales.application.dto.order.OrderReservationDto;

/** Reservas de stock de una orden. */
public interface FindOrderReservationsUseCase {

    java.util.List<OrderReservationDto> reservations(Long orderId);
}

package com.essenza.draco.modules.sales.application.input.order;

import java.util.List;

/** Cancela las órdenes pendientes cuya reserva venció (se ejecuta periódicamente). */
public interface ExpireOrderReservationsUseCase {

    List<Long> findExpired();

    /** @return true si la orden seguía pendiente y se canceló */
    boolean cancelExpired(Long orderId);
}

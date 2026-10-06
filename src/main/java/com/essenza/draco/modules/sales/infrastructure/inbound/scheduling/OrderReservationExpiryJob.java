package com.essenza.draco.modules.sales.infrastructure.inbound.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.essenza.draco.modules.sales.application.input.order.ExpireOrderReservationsUseCase;

/**
 * Cancela las órdenes pendientes cuya reserva venció ({@code essenza.orders.reservation-ttl},
 * 24 h por defecto) y libera su stock. Cada orden se procesa en su propia transacción.
 */
@Component
public class OrderReservationExpiryJob {

    private static final Logger log = LoggerFactory.getLogger(OrderReservationExpiryJob.class);

    private final ExpireOrderReservationsUseCase expire;

    public OrderReservationExpiryJob(ExpireOrderReservationsUseCase expire) {
        this.expire = expire;
    }

    @Scheduled(fixedDelayString = "${essenza.orders.expiry-check-interval:PT15M}",
            initialDelayString = "${essenza.orders.expiry-check-initial-delay:PT1M}")
    public void run() {
        int cancelled = 0;
        for (Long orderId : expire.findExpired()) {
            try {
                if (expire.cancelExpired(orderId)) {
                    cancelled++;
                }
            } catch (RuntimeException ex) {
                log.warn("No se pudo vencer la reserva de la orden {}: {}", orderId, ex.getMessage());
            }
        }
        if (cancelled > 0) {
            log.info("Reservas vencidas: {} orden(es) cancelada(s) y su stock liberado", cancelled);
        }
    }
}

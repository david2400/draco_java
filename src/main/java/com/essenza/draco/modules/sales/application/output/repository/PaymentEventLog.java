package com.essenza.draco.modules.sales.application.output.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Bitácora de pagos y de notificaciones de las pasarelas. */
public interface PaymentEventLog {

    record Event(Long id, Long paymentId, String source, String eventKey, String type, String fromStatus,
                 String toStatus, BigDecimal amount, String detail, String payload, Instant createdAt) {
    }

    /**
     * Registra un evento.
     *
     * @return {@code false} si ya existía un evento con la misma (source, eventKey): notificación repetida
     */
    boolean record(Event event);

    boolean exists(String source, String eventKey);

    List<Event> forPayment(Long paymentId);
}

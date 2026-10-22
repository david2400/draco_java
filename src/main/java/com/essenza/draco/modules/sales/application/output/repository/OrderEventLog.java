package com.essenza.draco.modules.sales.application.output.repository;

import java.time.Instant;
import java.util.List;

import com.essenza.draco.modules.sales.domain.model.order.OrderEventType;

/** Línea de tiempo de las órdenes (solo se agrega, nunca se edita). */
public interface OrderEventLog {

    record OrderEvent(Long id, Long orderId, OrderEventType type, String fromState, String toState, String message,
                      String actor, Instant createdAt) {
    }

    void append(Long orderId, OrderEventType type, String fromState, String toState, String message, String actor);

    List<OrderEvent> list(Long orderId);
}

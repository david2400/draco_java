package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.order;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.sales.application.output.repository.OrderEventLog;
import com.essenza.draco.modules.sales.domain.model.order.OrderEventType;

import jakarta.persistence.EntityManager;

/** Línea de tiempo en {@code order_events} (SQL nativo; solo inserciones). */
@Repository
public class OrderEventLogAdapter implements OrderEventLog {

    private final EntityManager em;

    public OrderEventLogAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    public void append(Long orderId, OrderEventType type, String fromState, String toState, String message, String actor) {
        em.createNativeQuery("""
                        INSERT INTO order_events (order_id, event_type, from_state, to_state, message, actor, created_at)
                        VALUES (:order, :type, :from, :to, :message, :actor, :at)""")
                .setParameter("order", orderId).setParameter("type", type.name()).setParameter("from", fromState)
                .setParameter("to", toState).setParameter("message", message).setParameter("actor", actor)
                .setParameter("at", Timestamp.valueOf(java.time.LocalDateTime.ofInstant(Instant.now(), java.time.ZoneOffset.UTC)))
                .executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderEvent> list(Long orderId) {
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT id_order_event, order_id, event_type, from_state, to_state, message, actor, created_at
                          FROM order_events WHERE order_id = :order ORDER BY created_at, id_order_event""")
                .setParameter("order", orderId).getResultList();
        return rows.stream().map(r -> new OrderEvent(((Number) r[0]).longValue(), ((Number) r[1]).longValue(),
                parse((String) r[2]), (String) r[3], (String) r[4], (String) r[5], (String) r[6], instant(r[7]))).toList();
    }

    private static OrderEventType parse(String value) {
        try {
            return OrderEventType.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return OrderEventType.NOTE;
        }
    }

    /** Las fechas se guardan en UTC (igual que Hibernate con los {@code Instant} de las entidades). */
    static Instant instant(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Instant i) {
            return i;
        }
        java.time.LocalDateTime local = value instanceof Timestamp ts ? ts.toLocalDateTime()
                : value instanceof java.time.LocalDateTime ldt ? ldt : Timestamp.valueOf(value.toString()).toLocalDateTime();
        return local.toInstant(java.time.ZoneOffset.UTC);
    }
}

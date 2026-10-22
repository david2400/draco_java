package com.essenza.draco.modules.sales.domain.model;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Estados de una orden y transiciones permitidas.
 * <pre>
 * PENDING → PAID | CANCELLED
 * PAID → PROCESSING | READY | SHIPPED | CANCELLED
 * PROCESSING → READY | SHIPPED | CANCELLED
 * READY → SHIPPED | DELIVERED (retiro en tienda) | CANCELLED
 * SHIPPED → DELIVERED
 * DELIVERED y CANCELLED son finales (las devoluciones siguen su propio flujo).
 * </pre>
 */
public enum OrderStatus {
    PENDING,
    PAID,
    PROCESSING,
    /** Preparada: lista para despachar o para que el cliente la retire (V16). */
    READY,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public Set<OrderStatus> next() {
        return switch (this) {
            case PENDING -> EnumSet.of(PAID, CANCELLED);
            case PAID -> EnumSet.of(PROCESSING, READY, SHIPPED, CANCELLED);
            case PROCESSING -> EnumSet.of(READY, SHIPPED, CANCELLED);
            case READY -> EnumSet.of(SHIPPED, DELIVERED, CANCELLED);
            case SHIPPED -> EnumSet.of(DELIVERED);
            case DELIVERED, CANCELLED -> EnumSet.noneOf(OrderStatus.class);
        };
    }

    public boolean canTransitionTo(OrderStatus target) {
        return next().contains(target);
    }

    /** El stock ya salió de la bodega (pagada y aún no entregada o cerrada). */
    public boolean stockCommitted() {
        return this == PAID || this == PROCESSING || this == READY || this == SHIPPED || this == DELIVERED;
    }

    public static Optional<OrderStatus> tryParse(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(value.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public static OrderStatus parse(String value) {
        return tryParse(value).orElseThrow(() -> new IllegalArgumentException(
                "Estado no válido: " + value + " (PENDING, PAID, PROCESSING, READY, SHIPPED, DELIVERED o CANCELLED)."));
    }
}

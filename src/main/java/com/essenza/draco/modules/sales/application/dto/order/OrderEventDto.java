package com.essenza.draco.modules.sales.application.dto.order;

import java.time.Instant;

/** Evento de la línea de tiempo: CREATED, STATUS, EDITED, LINES, DISCOUNT, PICKED, SHIPPED, DELIVERED o NOTE. */
public record OrderEventDto(Long id, String type, String fromState, String toState, String message, String actor,
                            Instant createdAt) {
}

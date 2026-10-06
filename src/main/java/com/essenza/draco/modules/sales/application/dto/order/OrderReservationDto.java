package com.essenza.draco.modules.sales.application.dto.order;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

/** Reserva de stock de una línea de la orden en una bodega. */
public record OrderReservationDto(
        Long id,
        Long lineId,
        Long skuId,
        Long warehouseId,
        int quantity,
        @Schema(allowableValues = {"ACTIVE", "COMMITTED", "RELEASED", "EXPIRED", "RETURNED"}) String status,
        Instant expiresAt) {
}

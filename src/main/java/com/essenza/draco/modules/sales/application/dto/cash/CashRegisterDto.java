package com.essenza.draco.modules.sales.application.dto.cash;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record CashRegisterDto(
        Long id,
        String code,
        String name,
        String location,
        @Schema(description = "Bodega propia de la caja (si no, la del punto de venta)") Long warehouseId,
        Boolean active,
        @Schema(description = "Turno abierto (si hay)") Long openSessionId,
        String openedBy,
        Instant openedAt,
        @Schema(description = "Efectivo esperado ahora en el turno abierto") BigDecimal expectedCash,
        @Schema(description = "Turnos registrados") Long sessionsCount,
        Long storeId,
        String storeName,
        @Schema(description = "Bodega de la que salen las ventas: la de la caja o la del punto de venta") Long effectiveWarehouseId,
        String deviceName,
        List<Long> allowedMethodIds,
        String openingMode,
        BigDecimal defaultOpeningAmount,
        BigDecimal cashLimit,
        @Schema(description = "El efectivo esperado del turno abierto supera el tope") Boolean overCashLimit,
        Boolean blindClose,
        BigDecimal differenceTolerance,
        BigDecimal maxDiscountPercent,
        Boolean autoInvoice,
        String receiptWidth,
        @Schema(description = "Textos del ticket (del punto de venta)") TicketInfo ticket) {

    /** Encabezado del ticket: datos del punto de venta. */
    public record TicketInfo(String storeName, String address, String city, String phone, String header, String footer) {
    }
}

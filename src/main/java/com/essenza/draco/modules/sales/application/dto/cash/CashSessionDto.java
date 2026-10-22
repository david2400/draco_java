package com.essenza.draco.modules.sales.application.dto.cash;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record CashSessionDto(
        Long id,
        Long cashRegisterId,
        String registerCode,
        String registerName,
        @Schema(description = "OPEN o CLOSED") String status,
        String openedBy,
        Instant openedAt,
        BigDecimal openingAmount,
        String openingNotes,
        String closedBy,
        Instant closedAt,
        @Schema(description = "Efectivo esperado: base + efectivo cobrado + entradas − salidas") BigDecimal expectedCash,
        BigDecimal countedCash,
        @Schema(description = "Contado − esperado (positivo = sobrante)") BigDecimal cashDifference,
        String closingNotes,
        @Schema(description = "Ventas (órdenes) del turno") Long salesCount,
        BigDecimal salesTotal,
        @Schema(description = "Cobrado en el turno (pagos aprobados, menos reembolsos)") BigDecimal collectedTotal,
        BigDecimal cashIn,
        BigDecimal cashOut,
        @Schema(description = "Pagos pendientes de confirmar en el turno") Long pendingPayments,
        List<CashSessionMethodDto> methods,
        List<CashMovementDto> movements,
        String storeName,
        @Schema(description = "Arqueo ciego: el cajero no ve el esperado hasta cerrar") Boolean blindClose,
        BigDecimal differenceTolerance,
        BigDecimal cashLimit,
        @Schema(description = "El efectivo esperado supera el tope de la caja") Boolean overCashLimit,
        @Schema(description = "Cerrado con una diferencia mayor que la tolerancia") Boolean exceedsTolerance) {
}

package com.essenza.draco.modules.sales.application.dto.payment;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Cobro de una orden: total, pagado, en proceso, saldo y pagos. */
public record OrderPaymentsDto(
        Long orderId,
        String orderState,
        BigDecimal orderTotal,
        @Schema(description = "Aprobado menos reembolsado") BigDecimal paidAmount,
        @Schema(description = "Pagos pendientes de confirmar") BigDecimal pendingAmount,
        BigDecimal balance,
        @Schema(description = "Lo que se puede cobrar ahora (saldo menos lo pendiente)") BigDecimal collectable,
        @Schema(description = "UNPAID, PARTIAL, PAID, OVERPAID") String paymentStatus,
        List<PaymentDto> payments) {
}

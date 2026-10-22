package com.essenza.draco.modules.sales.application.dto.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Pago de una orden. */
public record PaymentDto(
        Long id,
        Long orderId,
        Long paymentMethodId,
        String methodCode,
        String methodName,
        @Schema(description = "PHYSICAL | VIRTUAL") String channel,
        String kind,
        String provider,
        @Schema(description = "PENDING, APPROVED, DECLINED, EXPIRED, VOIDED, REFUNDED") String status,
        BigDecimal amount,
        BigDecimal feeAmount,
        @Schema(description = "Efectivo recibido") BigDecimal tenderedAmount,
        @Schema(description = "Cambio entregado") BigDecimal changeAmount,
        BigDecimal refundedAmount,
        @Schema(description = "Lo que aporta al saldo de la orden (aprobado menos reembolsado)") BigDecimal settledAmount,
        String currency,
        @Schema(description = "Referencia manual: voucher, comprobante, transacción") String reference,
        @Schema(description = "Referencia única del pago enviada a la pasarela") String paymentReference,
        @Schema(description = "Id de la transacción en la pasarela") String providerReference,
        @Schema(description = "Link de pago (pasarelas)") String checkoutUrl,
        String payerEmail,
        String notes,
        String statusDetail,
        Instant approvedAt,
        Instant expiresAt,
        Instant createdAt,
        @Schema(description = "Acciones posibles: confirm, decline, void, refund, sync") List<String> actions,
        @Schema(description = "Turno de caja en el que se cobró") Long cashSessionId) {
}

package com.essenza.draco.modules.sales.application.dto.payment;

import java.math.BigDecimal;
import java.util.List;

/** Totales del listado de pagos (mismos filtros), por medio y por estado. */
public record PaymentsSummaryDto(
        long count,
        BigDecimal approvedAmount,
        BigDecimal pendingAmount,
        BigDecimal refundedAmount,
        BigDecimal feeAmount,
        BigDecimal netAmount,
        List<ByMethod> byMethod,
        List<ByStatus> byStatus) {

    public record ByMethod(Long paymentMethodId, String name, String channel, long count, BigDecimal approvedAmount,
                           BigDecimal refundedAmount, BigDecimal feeAmount) {
    }

    public record ByStatus(String status, long count, BigDecimal amount) {
    }
}

package com.essenza.draco.modules.sales.application.dto.cash;

import java.math.BigDecimal;

/** Arqueo por medio de pago: esperado (pagos aprobados del turno) vs contado. */
public record CashSessionMethodDto(Long paymentMethodId, String name, String kind, Long paymentsCount, BigDecimal expectedAmount,
                                   BigDecimal countedAmount, BigDecimal difference) {
}

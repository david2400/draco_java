package com.essenza.draco.modules.sales.application.dto.payment;

import java.math.BigDecimal;
import java.time.Instant;

/** Evento de la bitácora de un pago (registro, confirmación, webhook, reembolso…). */
public record PaymentEventDto(Long id, String source, String type, String fromStatus, String toStatus,
                              BigDecimal amount, String detail, Instant createdAt) {
}

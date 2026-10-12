package com.essenza.draco.modules.sales.application.dto.payment;

import java.time.Instant;

/** Filtros del listado y del resumen de pagos (todos opcionales). */
public record PaymentFilter(String q, String status, Long paymentMethodId, String channel, String provider,
                            Long orderId, Instant from, Instant to) {
}

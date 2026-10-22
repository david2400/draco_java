package com.essenza.draco.shared.common.sales;

/** Una orden pasó a pagada (se publica dentro de la transacción; escuchar tras el commit). */
public record OrderPaidEvent(Long orderId) {
}

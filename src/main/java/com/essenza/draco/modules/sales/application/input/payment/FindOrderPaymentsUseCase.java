package com.essenza.draco.modules.sales.application.input.payment;

import com.essenza.draco.modules.sales.application.dto.payment.OrderPaymentsDto;

/** Cobro de una orden: saldo y pagos. */
public interface FindOrderPaymentsUseCase {

    OrderPaymentsDto forOrder(Long orderId);
}

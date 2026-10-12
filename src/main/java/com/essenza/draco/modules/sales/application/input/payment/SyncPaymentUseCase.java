package com.essenza.draco.modules.sales.application.input.payment;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;

/** Consulta en la pasarela el estado de un pago en línea y lo actualiza. */
public interface SyncPaymentUseCase {

    PaymentDto sync(Long id);
}

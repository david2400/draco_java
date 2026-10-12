package com.essenza.draco.modules.sales.application.input.payment;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentActionDto;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;

/** Rechaza un pago pendiente. */
public interface DeclinePaymentUseCase {

    PaymentDto decline(Long id, PaymentActionDto input);
}

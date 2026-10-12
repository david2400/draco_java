package com.essenza.draco.modules.sales.application.input.payment;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentActionDto;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;

/** Aprueba manualmente un pago pendiente. */
public interface ConfirmPaymentUseCase {

    PaymentDto confirm(Long id, PaymentActionDto input);
}

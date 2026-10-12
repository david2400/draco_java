package com.essenza.draco.modules.sales.application.input.payment;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentActionDto;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;

/** Anula un pago pendiente o aprobado sin reembolsos. */
public interface VoidPaymentUseCase {

    PaymentDto voidPayment(Long id, PaymentActionDto input);
}

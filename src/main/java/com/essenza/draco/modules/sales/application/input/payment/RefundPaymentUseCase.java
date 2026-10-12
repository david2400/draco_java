package com.essenza.draco.modules.sales.application.input.payment;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentActionDto;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;

/** Reembolsa un pago aprobado (parcial o total). */
public interface RefundPaymentUseCase {

    PaymentDto refund(Long id, PaymentActionDto input);
}

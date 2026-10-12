package com.essenza.draco.modules.sales.application.input.payment;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;
import com.essenza.draco.modules.sales.application.dto.payment.RegisterPaymentDto;

/** Registra un pago hecho en tienda o confirmado a mano (efectivo, datáfono, transferencia…). */
public interface RegisterPaymentUseCase {

    PaymentDto register(RegisterPaymentDto input);
}

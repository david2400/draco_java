package com.essenza.draco.modules.sales.application.input.payment;

import java.util.List;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentEventDto;

/** Bitácora de un pago. */
public interface FindPaymentEventsUseCase {

    List<PaymentEventDto> events(Long paymentId);
}

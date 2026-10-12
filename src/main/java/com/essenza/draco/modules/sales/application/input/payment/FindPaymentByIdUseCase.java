package com.essenza.draco.modules.sales.application.input.payment;

import java.util.Optional;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;

/** Consulta un pago. */
public interface FindPaymentByIdUseCase {

    Optional<PaymentDto> findById(Long id);
}

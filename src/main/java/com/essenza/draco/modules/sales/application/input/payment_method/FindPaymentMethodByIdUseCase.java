package com.essenza.draco.modules.sales.application.input.payment_method;

import java.util.Optional;

import com.essenza.draco.modules.sales.application.dto.payment_method.PaymentMethodDto;

/** Consulta un medio de pago. */
public interface FindPaymentMethodByIdUseCase {

    Optional<PaymentMethodDto> findById(Long id);
}

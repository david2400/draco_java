package com.essenza.draco.modules.sales.application.input.payment_type;

import com.essenza.draco.modules.sales.domain.dto.payment_type.PaymentTypeDto;

import java.util.Optional;

public interface FindPaymentTypeByIdUseCase {
    Optional<PaymentTypeDto> findById(Long id);
}

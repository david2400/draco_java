package com.essenza.draco.modules.sales.application.input.payment_type;

import com.essenza.draco.modules.sales.domain.dto.payment_type.CreatePaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.PaymentTypeDto;

public interface CreatePaymentTypeUseCase {
    PaymentTypeDto create(CreatePaymentTypeDto input);
}

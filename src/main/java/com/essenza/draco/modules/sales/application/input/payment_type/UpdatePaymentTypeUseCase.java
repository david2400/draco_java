package com.essenza.draco.modules.sales.application.input.payment_type;

import com.essenza.draco.modules.sales.domain.dto.payment_type.PaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.UpdatePaymentTypeDto;

public interface UpdatePaymentTypeUseCase {
    PaymentTypeDto update(Long id, UpdatePaymentTypeDto input);
}

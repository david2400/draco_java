package com.essenza.draco.modules.sales.application.input.payment_type;

import com.essenza.draco.modules.sales.domain.dto.payment_type.PaymentTypeDto;
import java.util.List;

public interface FindPaymentTypesUseCase {
    List<PaymentTypeDto> findAll();
}

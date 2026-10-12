package com.essenza.draco.modules.sales.application.input.payment_method;

import com.essenza.draco.modules.sales.application.dto.payment_method.PaymentMethodDto;
import com.essenza.draco.modules.sales.application.dto.payment_method.SavePaymentMethodDto;

/** Modifica un medio de pago. */
public interface UpdatePaymentMethodUseCase {

    PaymentMethodDto update(Long id, SavePaymentMethodDto input);
}

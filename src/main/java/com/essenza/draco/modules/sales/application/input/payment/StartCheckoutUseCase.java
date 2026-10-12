package com.essenza.draco.modules.sales.application.input.payment;

import com.essenza.draco.modules.sales.application.dto.payment.CheckoutDto;
import com.essenza.draco.modules.sales.application.dto.payment.StartCheckoutDto;

/** Inicia un pago en línea con la pasarela del medio elegido. */
public interface StartCheckoutUseCase {

    CheckoutDto start(StartCheckoutDto input);
}

package com.essenza.draco.modules.sales.application.input.payment_method;

import java.util.List;

import com.essenza.draco.modules.sales.application.dto.payment_method.PaymentMethodDto;

/** Lista los medios de pago ordenados por posición. */
public interface FindPaymentMethodsUseCase {

    /**
     * @param includeInactive incluir los inactivos (panel); el checkout solo usa activos
     * @param channel         PHYSICAL | VIRTUAL o {@code null} para todos
     */
    List<PaymentMethodDto> findAll(boolean includeInactive, String channel);
}

package com.essenza.draco.modules.sales.application.input.payment_method;



/** Borra un medio de pago sin pagos. */
public interface DeletePaymentMethodUseCase {

    /** Borra el medio; si ya tiene pagos responde 409 (desactívalo en su lugar). */
    void delete(Long id);
}

package com.essenza.draco.modules.sales.application.output.gateway;

import java.util.List;
import java.util.Optional;

import com.essenza.draco.modules.sales.domain.model.payment.GatewayProvider;

/** Pasarelas registradas en la aplicación. */
public interface PaymentGateways {

    Optional<PaymentGateway> find(GatewayProvider provider);

    List<PaymentGateway> all();

    /** URL pública del webhook de una pasarela (para registrarla en su panel). */
    String webhookUrl(GatewayProvider provider);

    /** URL de retorno por defecto cuando el checkout no trae una. */
    String defaultReturnUrl();
}

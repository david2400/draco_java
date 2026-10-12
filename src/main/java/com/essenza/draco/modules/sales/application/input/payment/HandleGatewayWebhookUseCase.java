package com.essenza.draco.modules.sales.application.input.payment;

import java.util.Map;

/** Notificaciones (webhooks) de las pasarelas. */
public interface HandleGatewayWebhookUseCase {

    /**
     * Procesa una notificación de la pasarela: verifica la firma, registra el evento (una sola
     * vez) y actualiza el pago.
     *
     * @return {@code true} si se aplicó un cambio
     */
    boolean handle(String provider, Map<String, String> headers, Map<String, String> query, String body);
}

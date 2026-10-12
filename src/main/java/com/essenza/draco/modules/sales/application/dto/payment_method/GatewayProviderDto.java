package com.essenza.draco.modules.sales.application.dto.payment_method;

import io.swagger.v3.oas.annotations.media.Schema;

/** Pasarela disponible y si tiene credenciales en el servidor. */
public record GatewayProviderDto(
        @Schema(description = "SANDBOX, WOMPI o MERCADO_PAGO") String code,
        boolean configured,
        @Schema(description = "test | production | sandbox") String environment,
        @Schema(description = "URL que debe registrarse en la pasarela para los webhooks") String webhookUrl) {
}

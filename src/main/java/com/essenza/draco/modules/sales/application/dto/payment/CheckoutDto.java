package com.essenza.draco.modules.sales.application.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

/** Pago en línea iniciado: el cliente debe ir a {@code checkoutUrl}. */
public record CheckoutDto(PaymentDto payment, @Schema(description = "Link de pago de la pasarela") String checkoutUrl) {
}

package com.essenza.draco.modules.sales.application.dto.payment;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Inicia un pago en línea con una pasarela. */
public record StartCheckoutDto(
        @NotNull Long orderId,
        @NotNull Long paymentMethodId,
        @Schema(description = "Vacío = saldo pendiente de la orden") @DecimalMin(value = "0.01") BigDecimal amount,
        @Email @Size(max = 160) String payerEmail,
        @Schema(description = "A dónde vuelve el cliente al terminar (vacío = la configurada)") @Size(max = 1000) String returnUrl) {
}

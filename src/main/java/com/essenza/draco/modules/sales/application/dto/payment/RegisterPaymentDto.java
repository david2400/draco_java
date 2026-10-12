package com.essenza.draco.modules.sales.application.dto.payment;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Pago registrado por quien cobra (efectivo, datáfono, transferencia, billetera…). */
public record RegisterPaymentDto(
        @NotNull Long orderId,
        @NotNull Long paymentMethodId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @Schema(description = "Efectivo recibido (para calcular el cambio)") BigDecimal tenderedAmount,
        @Size(max = 120) String reference,
        @Size(max = 500) String notes,
        @Schema(description = "Dejarlo pendiente de confirmación (p. ej. transferencia aún no vista en el banco)") Boolean pending) {
}

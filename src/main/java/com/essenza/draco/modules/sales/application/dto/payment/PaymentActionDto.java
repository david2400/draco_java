package com.essenza.draco.modules.sales.application.dto.payment;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/** Datos de una acción sobre un pago (motivo, referencia, monto a reembolsar). */
public record PaymentActionDto(
        @Size(max = 255) String reason,
        @Schema(description = "Confirmar: referencia del comprobante si el medio la exige") @Size(max = 120) String reference,
        @Schema(description = "Reembolso: monto (vacío = todo lo reembolsable)") BigDecimal amount) {
}

package com.essenza.draco.modules.devolution.application.dto.flow;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Reembolso: por defecto todo el valor acreditado sobre los pagos de la orden. */
public record RefundReturnDto(
        @Size(max = 100) String by,
        Long refundMethodId,
        @Schema(description = "Dinero a devolver (vacío = valor acreditado; 0 = saldo a favor)") @PositiveOrZero BigDecimal amount,
        @Size(max = 255) String notes) {
}

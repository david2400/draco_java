package com.essenza.draco.modules.sales.application.dto.cash;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCashMovementDto(
        @NotBlank @Schema(description = "IN (entrada) u OUT (salida)") String type,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Size(max = 255) String reason,
        @Size(max = 120) String reference,
        @Size(max = 120) String createdBy) {
}

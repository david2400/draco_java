package com.essenza.draco.modules.sales.application.dto.cash;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OpenCashSessionDto(
        @NotNull Long cashRegisterId,
        @NotBlank @Size(max = 120) @Schema(description = "Quién abre (cajero)") String openedBy,
        @DecimalMin("0") @Schema(description = "Base en efectivo") BigDecimal openingAmount,
        @Size(max = 500) String notes) {
}

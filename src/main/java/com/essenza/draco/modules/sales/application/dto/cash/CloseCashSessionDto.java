package com.essenza.draco.modules.sales.application.dto.cash;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CloseCashSessionDto(
        @NotBlank @Size(max = 120) String closedBy,
        @NotNull @DecimalMin("0") @Schema(description = "Efectivo contado en la caja") BigDecimal countedCash,
        @Schema(description = "Conteo de otros medios (vouchers, transferencias); los omitidos se toman como esperados")
        List<MethodCount> counts,
        @Size(max = 500) String notes) {

    public record MethodCount(@NotNull Long paymentMethodId, @NotNull @DecimalMin("0") BigDecimal countedAmount) {
    }
}

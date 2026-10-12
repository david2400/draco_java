package com.essenza.draco.modules.sales.application.dto.payment_method;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Alta o edición de un medio de pago. {@code code} vacío se deriva del nombre. */
public record SavePaymentMethodDto(
        @Size(max = 40) String code,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 255) String description,
        @Size(max = 1000) String instructions,
        @NotBlank @Schema(description = "PHYSICAL | VIRTUAL") String channel,
        @NotBlank String kind,
        String provider,
        Boolean active,
        Integer position,
        BigDecimal feePercent,
        BigDecimal feeFixed,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Boolean requiresReference,
        Boolean allowsChange) {
}

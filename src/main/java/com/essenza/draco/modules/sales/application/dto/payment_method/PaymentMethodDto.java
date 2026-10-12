package com.essenza.draco.modules.sales.application.dto.payment_method;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

/** Medio de pago (efectivo, datáfono, transferencia, pasarela…). */
public record PaymentMethodDto(
        Long id,
        String code,
        String name,
        String description,
        @Schema(description = "Instrucciones para quien cobra o para el cliente") String instructions,
        @Schema(description = "PHYSICAL | VIRTUAL") String channel,
        @Schema(description = "CASH, CARD_TERMINAL, BANK_TRANSFER, DIGITAL_WALLET, GATEWAY, CASH_ON_DELIVERY, OTHER") String kind,
        @Schema(description = "Pasarela: SANDBOX, WOMPI o MERCADO_PAGO (solo GATEWAY)") String provider,
        boolean active,
        int position,
        BigDecimal feePercent,
        BigDecimal feeFixed,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        boolean requiresReference,
        boolean allowsChange,
        @Schema(description = "Pasarelas: si las credenciales están configuradas en el servidor") boolean providerConfigured,
        @Schema(description = "Pagos registrados con este medio") long paymentsCount) {
}

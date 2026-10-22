package com.essenza.draco.modules.inventory.application.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;

/** Contadores del listado de proveedores. */
public record SupplierStatsDto(
        long total,
        long active,
        long blocked,
        long inactive,
        @Schema(description = "Proveedores sin documento (registrados antes de V13)") long withoutDocument,
        @Schema(description = "Proveedores con plazo de pago (crédito)") long credit) {
}

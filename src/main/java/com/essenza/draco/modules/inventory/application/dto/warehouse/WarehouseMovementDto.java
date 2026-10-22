package com.essenza.draco.modules.inventory.application.dto.warehouse;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/** Movimiento de inventario visto desde una bodega. */
public record WarehouseMovementDto(
        Long id,
        String type,
        @Schema(description = "IN (entra a la bodega) u OUT (sale)") String direction,
        Long skuId,
        String skuCode,
        String productName,
        int quantity,
        @Schema(description = "Bodega de origen o destino en transferencias") String otherWarehouse,
        String reason,
        String referenceType,
        Long referenceId,
        Instant createdAt) {
}

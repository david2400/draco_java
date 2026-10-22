package com.essenza.draco.modules.sales.application.dto.cash;

import io.swagger.v3.oas.annotations.media.Schema;

public record StoreDto(
        Long id,
        String code,
        String name,
        String address,
        String city,
        String phone,
        String email,
        @Schema(description = "Bodega de la que salen las ventas de mostrador") Long warehouseId,
        String openingHours,
        String ticketHeader,
        String ticketFooter,
        Boolean active,
        Long registersCount,
        @Schema(description = "Cajas con turno abierto") Long openRegisters,
        Long countryId,
        Long stateId,
        Long cityId) {
}

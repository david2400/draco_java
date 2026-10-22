package com.essenza.draco.modules.inventory.application.dto.warehouse;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/** Bodega con su resumen de existencias. */
public record WarehouseDto(
        Long id,
        String code,
        String name,
        String warehouseType,
        @Schema(description = "Bodega principal (recibe los ajustes desde la ficha de producto)") boolean isMain,
        boolean active,
        boolean allowSales,
        boolean allowOnline,
        boolean allowReceiving,
        int fulfillmentPriority,
        String address,
        Long countryId,
        Long stateId,
        Long cityId,
        String city,
        String managerName,
        String managerPhone,
        String managerEmail,
        String openingHours,
        String notes,
        BigDecimal areaM2,
        Integer capacityUnits,
        Integer capacityPositions,
        @Schema(description = "SKU con existencias o mínimo registrado") long skuCount,
        long unitsOnHand,
        long unitsReserved,
        long unitsAvailable,
        @Schema(description = "Valor de las existencias al costo del SKU") BigDecimal stockValue,
        @Schema(description = "SKU por debajo del mínimo") long belowMinCount,
        @Schema(description = "Ocupación (%) sobre la capacidad en unidades") BigDecimal occupancyPercent,
        long locationsCount,
        @Schema(description = "Tiendas que venden desde esta bodega") long storesCount) {
}

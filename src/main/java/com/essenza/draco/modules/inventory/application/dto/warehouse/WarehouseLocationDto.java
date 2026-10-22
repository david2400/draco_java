package com.essenza.draco.modules.inventory.application.dto.warehouse;

public record WarehouseLocationDto(Long id, Long warehouseId, String code, String zone, String aisle, String rack,
                                   String level, String locationType, Integer capacityUnits, boolean active) {
}

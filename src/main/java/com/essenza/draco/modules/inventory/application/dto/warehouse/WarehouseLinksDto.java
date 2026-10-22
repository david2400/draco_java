package com.essenza.draco.modules.inventory.application.dto.warehouse;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** Tiendas y cajas que venden desde la bodega. */
public record WarehouseLinksDto(List<LinkedStore> stores, List<LinkedRegister> registers) {

    @Schema(name = "WarehouseLinkedStore")
    public record LinkedStore(Long id, String code, String name, boolean active) {
    }

    @Schema(name = "WarehouseLinkedRegister")
    public record LinkedRegister(Long id, String name, String storeName, boolean active,
                                 @Schema(description = "La caja tiene la bodega asignada directamente (no por su tienda)") boolean own) {
    }
}

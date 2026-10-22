package com.essenza.draco.modules.inventory.application.dto.warehouse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/** Ubicación interna de la bodega. */
public record SaveLocationDto(
        @Size(max = 30) @Schema(description = "Vacío = zona-pasillo-estante-nivel") String code,
        @Size(max = 30) String zone,
        @Size(max = 10) String aisle,
        @Size(max = 10) String rack,
        @Size(max = 10) String level,
        @Schema(description = "STORAGE, PICKING, RECEIVING, DISPATCH o QUARANTINE") String locationType,
        Integer capacityUnits,
        Boolean active) {
}

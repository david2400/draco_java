package com.essenza.draco.modules.devolution.application.dto.flow;

import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Inspección: estado de cada producto, si vuelve al stock y cargo de reposición. */
public record InspectReturnDto(
        @Size(max = 100) String by,
        @Size(max = 255) String notes,
        @Schema(description = "Bodega de reingreso (vacía = principal)") Long warehouseId,
        @Valid List<Line> lines) {

    @Schema(name = "InspectReturnLine")
    public record Line(
            @NotNull Long detailId,
            @NotNull @Schema(description = "NEW, OPENED, DAMAGED, DEFECTIVE") String condition,
            @Schema(description = "Vuelve al stock (vacío = según el estado: nuevo/abierto sí)") Boolean restock,
            @PositiveOrZero BigDecimal restockingFee) {
    }
}

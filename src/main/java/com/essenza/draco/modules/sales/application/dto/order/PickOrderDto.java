package com.essenza.draco.modules.sales.application.dto.order;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Unidades preparadas por línea. */
public record PickOrderDto(@NotEmpty @Valid List<Line> lines, @Size(max = 120) String actor) {

    @Schema(name = "PickOrderLine")
    public record Line(@NotNull Long lineId, @NotNull Integer pickedQuantity) {
    }
}

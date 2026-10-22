package com.essenza.draco.modules.devolution.application.dto.flow;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Recepción física: unidades que llegaron por línea (las omitidas se reciben completas). */
public record ReceiveReturnDto(
        @Size(max = 100) String by,
        @Size(max = 255) String notes,
        @Valid List<Line> lines) {

    @Schema(name = "ReceiveReturnLine")
    public record Line(
            @NotNull @Schema(description = "id de la línea de la devolución") Long detailId,
            @NotNull @Min(0) Integer receivedQuantity) {
    }
}

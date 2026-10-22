package com.essenza.draco.modules.devolution.application.dto.flow;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Solicitud de devolución con sus líneas (precio y producto se toman de la orden). */
public record ReturnRequestDto(
        @NotNull @Positive Long orderId,
        @NotNull @Positive Long motiveDevolutionId,
        @NotBlank @Size(max = 255) String observation,
        Long returnMethodId,
        Long refundMethodId,
        @Size(max = 50) String externalReference,
        @NotEmpty @Valid List<Line> lines) {

    @Schema(name = "ReturnRequestLine")
    public record Line(
            @NotNull @Positive Long productOrderId,
            @NotNull @Positive Integer quantity,
            @Schema(description = "Detalle del cliente sobre este producto") @Size(max = 255) String observation) {
    }
}

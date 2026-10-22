package com.essenza.draco.modules.sales.application.dto.order;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Orden completa en un paso: cabecera y productos (reserva el stock de cada línea). */
public record CreateFullOrderDto(
        @NotNull @Valid SaveOrderDetailsDto details,
        @NotEmpty @Size(max = 200) @Valid List<Line> lines) {

    @Schema(name = "CreateFullOrderLine")
    public record Line(Long productId, Long skuId, @NotNull @Positive Integer quantity, BigDecimal discount) {
    }
}

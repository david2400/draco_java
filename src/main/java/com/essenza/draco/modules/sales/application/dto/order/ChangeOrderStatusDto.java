package com.essenza.draco.modules.sales.application.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

/** Cambio de estado de una orden (aplica reservas, descuento o devolución de stock). */
public record ChangeOrderStatusDto(
        @NotBlank @Schema(allowableValues = {"PENDING", "PAID", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"}) String state,
        @Size(max = 200) @Schema(description = "Motivo (se guarda al cancelar)") String reason) {
}

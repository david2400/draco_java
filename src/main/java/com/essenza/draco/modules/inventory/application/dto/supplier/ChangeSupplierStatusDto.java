package com.essenza.draco.modules.inventory.application.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeSupplierStatusDto(
        @NotBlank @Schema(description = "ACTIVE, BLOCKED o INACTIVE") String status,
        @Size(max = 255) @Schema(description = "Motivo (obligatorio al bloquear)") String reason) {
}

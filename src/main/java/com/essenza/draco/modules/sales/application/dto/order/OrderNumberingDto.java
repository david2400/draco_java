package com.essenza.draco.modules.sales.application.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/** Numeración de órdenes. {@code preview} (solo lectura) es el próximo número. */
public record OrderNumberingDto(@NotBlank String prefix, @NotNull Integer padding, @NotNull Long nextNumber,
                                @Schema(accessMode = Schema.AccessMode.READ_ONLY) String preview) {
}

package com.essenza.draco.modules.sales.application.dto.order;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/** Cambio de estado de varias órdenes (cada una con sus reglas). */
public record BulkOrderStatusDto(@NotEmpty @Size(max = 200) List<Long> ids, @NotBlank String state,
                                 @Size(max = 200) String reason, @Size(max = 120) String actor) {
}

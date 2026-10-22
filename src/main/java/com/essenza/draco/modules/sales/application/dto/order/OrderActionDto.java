package com.essenza.draco.modules.sales.application.dto.order;

import jakarta.validation.constraints.Size;

/** Acción sobre la orden (lista, entregada, nota) con un comentario opcional. */
public record OrderActionDto(@Size(max = 1000) String note, @Size(max = 120) String actor) {
}

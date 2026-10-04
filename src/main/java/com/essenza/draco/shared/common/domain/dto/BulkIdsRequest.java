package com.essenza.draco.shared.common.domain.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de las operaciones en lote (p. ej. eliminar varios registros).
 * El límite evita peticiones abusivas que bloqueen la transacción.
 */
public record BulkIdsRequest(
        @NotEmpty
        @Size(max = 500)
        List<@NotNull @Positive Long> ids) {
}

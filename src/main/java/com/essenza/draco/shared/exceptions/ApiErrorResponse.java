package com.essenza.draco.shared.exceptions;

import java.time.Instant;
import java.util.List;

/**
 * Cuerpo de error uniforme. El frontend muestra {@code message} y, en
 * errores de validación, puede marcar cada campo con {@code fieldErrors}.
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        List<FieldError> fieldErrors) {

    public record FieldError(String field, String message) {
    }
}

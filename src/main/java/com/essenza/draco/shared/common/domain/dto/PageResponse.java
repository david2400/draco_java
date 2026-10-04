package com.essenza.draco.shared.common.domain.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

/**
 * Respuesta paginada estable para el API.
 *
 * Se evita serializar {@link Page} directamente: su JSON no es un contrato
 * estable (Spring Data lo advierte desde 3.3) y expone detalles internos
 * como {@code pageable}. Este DTO fija el formato que consume el frontend.
 *
 * @param content       elementos de la página actual
 * @param page          índice de página (base 0)
 * @param size          tamaño de página solicitado
 * @param totalElements total de registros que cumplen el filtro
 * @param totalPages    total de páginas
 * @param sort          orden aplicado ("campo,asc;campo2,desc") o vacío
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        String sort) {

    public static <T> PageResponse<T> from(Page<T> page) {
        String sort = page.getSort().isSorted()
                ? page.getSort().stream()
                        .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                        .reduce((a, b) -> a + ";" + b)
                        .orElse("")
                : "";
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                sort);
    }

    public static <S, T> PageResponse<T> from(Page<S> page, Function<S, T> mapper) {
        return from(page.map(mapper));
    }
}

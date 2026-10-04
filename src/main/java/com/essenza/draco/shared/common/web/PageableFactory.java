package com.essenza.draco.shared.common.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Construye un {@link Pageable} seguro a partir de los parámetros de la URL.
 *
 * <ul>
 *   <li>{@code page} base 0, nunca negativo.</li>
 *   <li>{@code size} acotado a [1, {@value #MAX_SIZE}] (por defecto {@value #DEFAULT_SIZE}).</li>
 *   <li>{@code sort} con formato {@code campo,asc;campo2,desc}; solo se aceptan
 *       campos de la lista blanca, para no exponer columnas internas ni
 *       provocar errores 500 por propiedades inexistentes.</li>
 * </ul>
 */
public final class PageableFactory {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 200;

    private PageableFactory() {
    }

    public static Pageable of(Integer page, Integer size, String sort, Set<String> allowedSorts, Sort defaultSort) {
        int p = page != null ? Math.max(page, 0) : 0;
        int s = size != null ? Math.min(Math.max(size, 1), MAX_SIZE) : DEFAULT_SIZE;
        Sort parsed = parseSort(sort, allowedSorts);
        return PageRequest.of(p, s, parsed.isSorted() ? parsed : defaultSort);
    }

    static Sort parseSort(String sortParam, Set<String> allowedSorts) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (String part : sortParam.split(";")) {
            String[] segments = part.trim().split(",");
            if (segments.length == 0 || segments[0].isBlank()) {
                continue;
            }
            String property = segments[0].trim();
            if (!allowedSorts.contains(property)) {
                continue;
            }
            Sort.Direction direction = segments.length > 1 && "desc".equalsIgnoreCase(segments[1].trim())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            orders.add(new Sort.Order(direction, property).ignoreCase());
        }
        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }
}

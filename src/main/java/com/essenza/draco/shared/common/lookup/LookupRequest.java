package com.essenza.draco.shared.common.lookup;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Parámetros normalizados de una búsqueda ligera.
 *
 * <ul>
 *   <li>{@code q}: texto libre (recortado; vacío = sin filtro de texto).</li>
 *   <li>{@code ids}: ids concretos separados por coma, para mostrar el valor ya elegido.
 *       Si vienen ids se ignora {@code q}.</li>
 *   <li>{@code limit}: entre 1 y {@value #MAX_LIMIT} (por defecto {@value #DEFAULT_LIMIT}).</li>
 * </ul>
 */
public record LookupRequest(String q, List<Long> ids, int limit) {

    public static final int DEFAULT_LIMIT = 20;
    public static final int MAX_LIMIT = 50;

    public LookupRequest {
        q = q == null ? "" : q.trim();
        ids = ids == null ? List.of() : List.copyOf(ids);
        limit = Math.min(Math.max(limit, 1), MAX_LIMIT);
    }

    public static LookupRequest of(String q, String ids, Integer limit) {
        List<Long> parsed = ids == null || ids.isBlank() ? List.of() : Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> s.matches("\\d{1,18}"))
                .map(Long::valueOf)
                .distinct()
                .limit(MAX_LIMIT)
                .toList();
        int size = limit == null ? DEFAULT_LIMIT : limit;
        return new LookupRequest(q, parsed, parsed.isEmpty() ? size : Math.max(size, parsed.size()));
    }

    public boolean byIds() {
        return !ids.isEmpty();
    }

    public boolean hasText() {
        return !q.isEmpty();
    }

    /** Patrón LIKE en minúsculas con {@code %} y {@code _} escapados (escape {@code \}). */
    public String containsPattern() {
        return "%" + escaped() + "%";
    }

    /** Patrón LIKE para "empieza por" (se usa para ordenar primero esas coincidencias). */
    public String prefixPattern() {
        return escaped() + "%";
    }

    private String escaped() {
        return Objects.requireNonNull(q).toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}

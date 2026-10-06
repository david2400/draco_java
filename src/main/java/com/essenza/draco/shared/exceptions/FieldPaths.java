package com.essenza.draco.shared.exceptions;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;

/**
 * Traduce rutas de propiedades Java ({@code items[0].unitPrice}) al nombre que
 * viaja en el JSON ({@code items[0].unit_price}), con la misma estrategia que
 * {@code spring.jackson.property-naming-strategy=SNAKE_CASE}.
 */
final class FieldPaths {

    private static final PropertyNamingStrategies.SnakeCaseStrategy SNAKE =
            new PropertyNamingStrategies.SnakeCaseStrategy();

    private FieldPaths() {
    }

    static String toSnakeCase(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        return Arrays.stream(path.split("\\.", -1))
                .map(FieldPaths::segment)
                .collect(Collectors.joining("."));
    }

    private static String segment(String segment) {
        int bracket = segment.indexOf('[');
        String name = bracket >= 0 ? segment.substring(0, bracket) : segment;
        String suffix = bracket >= 0 ? segment.substring(bracket) : "";
        return SNAKE.translate(name) + suffix;
    }
}

package com.essenza.draco.modules.catalog.domain.model;

import java.util.Locale;

/**
 * Estado editorial del producto en el catálogo.
 *
 * <ul>
 *   <li>{@code DRAFT}: en preparación, no se vende.</li>
 *   <li>{@code ACTIVE}: publicado (equivale al antiguo {@code available = true}).</li>
 *   <li>{@code INACTIVE}: pausado temporalmente.</li>
 *   <li>{@code ARCHIVED}: retirado del catálogo; se conserva por historial.</li>
 * </ul>
 */
public enum ProductStatus {
    DRAFT,
    ACTIVE,
    INACTIVE,
    ARCHIVED;

    /** Solo un producto activo está publicado (se vende si además tiene stock). */
    public boolean isListed() {
        return this == ACTIVE;
    }

    /** Convierte texto en estado; {@code null} o vacío devuelve {@code null}. */
    public static ProductStatus parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado de producto no válido: " + value);
        }
    }

    /**
     * Compatibilidad con el booleano {@code available} del API anterior: activar
     * publica; desactivar pausa, salvo que el producto ya esté en borrador o archivado.
     */
    public static ProductStatus fromAvailability(boolean available, ProductStatus current) {
        if (available) {
            return ACTIVE;
        }
        if (current == DRAFT || current == ARCHIVED) {
            return current;
        }
        return INACTIVE;
    }
}

package com.essenza.draco.shared.common.lookup;

/**
 * Opción ligera para selectores con búsqueda (combobox asíncrono del panel).
 *
 * @param id    identificador
 * @param label texto principal
 * @param hint  texto secundario opcional (código, categoría padre, etc.)
 */
public record LookupOption(Long id, String label, String hint) {
}

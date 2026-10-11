package com.essenza.draco.modules.catalog.application.dto.attribute;

import java.util.List;

/**
 * Dónde se usa un atributo.
 *
 * @param templates    plantillas que lo incluyen (obligatorio / eje de variante)
 * @param productCount productos con valor en su ficha técnica
 * @param variantCount SKUs que lo usan como eje de variante
 * @param products     algunos productos con valor (máx. 10), para enlazarlos
 * @param options      uso de cada opción (productos + SKUs); solo las usadas
 */
public record AttributeUsageDto(
        List<TemplateRef> templates,
        long productCount,
        long variantCount,
        List<ProductRef> products,
        List<OptionUsage> options) {

    public record TemplateRef(Long id, String name, boolean required, boolean variantAxis) {
    }

    public record ProductRef(Long id, String name) {
    }

    public record OptionUsage(Long optionId, long count) {
    }
}

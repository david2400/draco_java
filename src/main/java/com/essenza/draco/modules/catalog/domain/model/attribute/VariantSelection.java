package com.essenza.draco.modules.catalog.domain.model.attribute;

import java.util.Map;
import java.util.Objects;

/** Opciones de los ejes de variante elegidas para un SKU (atributo → opción). */
public record VariantSelection(Long skuId, Map<Long, Long> optionsByAttribute) {

    public VariantSelection {
        Objects.requireNonNull(skuId, "skuId");
        optionsByAttribute = optionsByAttribute == null ? Map.of() : Map.copyOf(optionsByAttribute);
    }
}

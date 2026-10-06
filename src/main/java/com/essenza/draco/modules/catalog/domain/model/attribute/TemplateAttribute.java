package com.essenza.draco.modules.catalog.domain.model.attribute;

import java.util.Objects;

/**
 * Atributo dentro de una plantilla.
 *
 * @param required    obligatorio para publicar el producto (estado ACTIVE)
 * @param variantAxis define las variantes (color, talla): su valor va en cada SKU, no en el producto
 * @param filterable  la tienda puede filtrar por él
 */
public record TemplateAttribute(Long attributeId, boolean required, boolean variantAxis, boolean filterable, int position) {

    public TemplateAttribute {
        Objects.requireNonNull(attributeId, "attributeId");
    }
}

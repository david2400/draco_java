package com.essenza.draco.modules.catalog.application.output.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeValue;
import com.essenza.draco.modules.catalog.domain.model.attribute.VariantSelection;

/** Plantilla, ficha técnica y ejes de variante de un producto. */
public interface ProductAttributesStore {

    record ProductHeader(Long id, String status, Long templateId) {
    }

    record VariantSku(Long id, String code, String name, boolean active) {
    }

    Optional<ProductHeader> findProduct(Long productId);

    /** SKU de variante (no borrados) del producto. */
    List<VariantSku> variantSkus(Long productId);

    void setTemplate(Long productId, Long templateId);

    List<AttributeValue> values(Long productId);

    void replaceValues(Long productId, List<AttributeValue> values);

    /** Opciones por SKU: sku → (atributo → opción). */
    Map<Long, Map<Long, Long>> skuOptions(Collection<Long> skuIds);

    /** Reemplaza las opciones de los SKU indicados. */
    void replaceSkuOptions(Collection<Long> skuIds, List<VariantSelection> selections);
}

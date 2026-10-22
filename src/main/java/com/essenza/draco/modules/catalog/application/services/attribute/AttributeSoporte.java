package com.essenza.draco.modules.catalog.application.services.attribute;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.ProductHeader;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.VariantSku;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore;
import com.essenza.draco.modules.catalog.application.output.repository.ProductTemplateRepository;
import com.essenza.draco.modules.catalog.application.services.attribute.AttributeSoporte.Snapshot;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeValue;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductAttributesPolicy;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductTemplate;
import com.essenza.draco.modules.catalog.domain.model.attribute.VariantSelection;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Código compartido por los casos de uso de attribute (antes en ProductAttributesServiceImpl). */
@Component
@RequiredArgsConstructor
class AttributeSoporte {

    private final ProductAttributesStore store;
    private final ProductTemplateRepository templates;
    private final AttributeRepository attributes;

    /** Estado actual del producto, ya cargado. */
    record Snapshot(ProductHeader header, ProductTemplate template, Map<Long, Attribute> definitions,
                            List<AttributeValue> values, List<VariantSku> skus, List<VariantSelection> selections,
                            Set<Long> productAxes) {

        Set<Long> skuIds() {
            return skus.stream().map(VariantSku::id).collect(Collectors.toCollection(LinkedHashSet::new));
        }

        List<String> missing() {
            return ProductAttributesPolicy.missing(template, productAxes, definitions, values, selections, skuIds());
        }
    }

    ProductHeader header(Long productId) {
        return store.findProduct(productId)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + productId));
    }

    Snapshot load(Long productId) {
        ProductHeader header = header(productId);
        ProductTemplate template = header.templateId() == null ? null
                : templates.findById(header.templateId()).orElse(null);
        List<AttributeValue> values = store.values(productId);
        Set<Long> productAxes = store.productAxes(productId);
        List<VariantSku> skus = store.variantSkus(productId);
        Map<Long, Map<Long, Long>> options = store.skuOptions(skus.stream().map(VariantSku::id).toList());
        List<VariantSelection> selections = skus.stream()
                .map(sku -> new VariantSelection(sku.id(), options.getOrDefault(sku.id(), Map.of())))
                .toList();
        Map<Long, Attribute> definitions;
        if (template != null) {
            // Los de la plantilla y los adicionales que tienen valor.
            Set<Long> ids = new LinkedHashSet<>();
            template.getAttributes().forEach(item -> ids.add(item.attributeId()));
            values.forEach(value -> ids.add(value.attributeId()));
            ids.addAll(productAxes);
            definitions = definitionsOf(ids);
        } else {
            definitions = attributes.findAll().stream().collect(Collectors.toMap(Attribute::getId, Function.identity(),
                    (left, right) -> left, LinkedHashMap::new));
        }
        return new Snapshot(header, template, definitions, values, skus, selections, productAxes);
    }

    Map<Long, Attribute> definitionsOf(java.util.Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return attributes.findByIds(ids).stream().collect(Collectors.toMap(Attribute::getId, Function.identity()));
    }
}

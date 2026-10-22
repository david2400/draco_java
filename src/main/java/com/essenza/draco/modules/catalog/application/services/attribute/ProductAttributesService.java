package com.essenza.draco.modules.catalog.application.services.attribute;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeValueDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.ProductAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.ProductTemplateDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveProductAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.VariantAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.VariantOptionDto;
import com.essenza.draco.modules.catalog.application.input.attribute.ProductAttributesUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.ProductHeader;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.VariantSku;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore;
import com.essenza.draco.modules.catalog.application.output.repository.ProductTemplateRepository;
import com.essenza.draco.modules.catalog.application.services.attribute.AttributeSoporte.Snapshot;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeRuleViolation;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeValue;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductAttributesPolicy;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductTemplate;
import com.essenza.draco.modules.catalog.domain.model.attribute.TemplateAttribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.VariantSelection;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductAttributesService implements ProductAttributesUseCase {

    private final ProductAttributesStore store;
    private final ProductTemplateRepository templates;
    private final AttributeDtosService attributeService;
    private final ProductTemplateDtosService templateService;
    private final AttributeSoporte soporte;

    private static final String ACTIVE = "ACTIVE";

    @Override
    @Transactional(readOnly = true)
    public ProductAttributesDto get(Long productId) {
        return toDto(soporte.load(productId));
    }

    @Override
    public ProductAttributesDto save(Long productId, SaveProductAttributesDto input) {
        ProductHeader header = soporte.header(productId);
        ProductTemplate template = null;
        if (input.templateId() != null) {
            template = templates.findById(input.templateId())
                    .orElseThrow(() -> new AttributeRuleViolation("La plantilla " + input.templateId() + " no existe."));
        }
        List<AttributeValue> values = (input.values() == null ? List.<AttributeValueDto>of() : input.values()).stream()
                .map(value -> new AttributeValue(value.attributeId(), value.valueText(), value.valueNumber(),
                        value.valueBoolean(), value.optionId()))
                .toList();
        List<VariantSelection> selections = new ArrayList<>();
        for (VariantAttributesDto variant : input.variants() == null ? List.<VariantAttributesDto>of() : input.variants()) {
            Map<Long, Long> options = new LinkedHashMap<>();
            for (VariantOptionDto option : variant.options() == null ? List.<VariantOptionDto>of() : variant.options()) {
                if (options.put(option.attributeId(), option.optionId()) != null) {
                    throw new AttributeRuleViolation("Un eje está repetido en la variante " + variant.skuId() + ".");
                }
            }
            selections.add(new VariantSelection(variant.skuId(), options));
        }

        Set<Long> definitionIds = new LinkedHashSet<>();
        if (template != null) {
            template.getAttributes().forEach(item -> definitionIds.add(item.attributeId()));
        }
        values.forEach(value -> definitionIds.add(value.attributeId()));
        selections.forEach(selection -> definitionIds.addAll(selection.optionsByAttribute().keySet()));
        Map<Long, Attribute> definitions = soporte.definitionsOf(definitionIds);

        List<VariantSku> skus = store.variantSkus(productId);
        Set<Long> skuIds = skus.stream().map(VariantSku::id).collect(Collectors.toCollection(LinkedHashSet::new));
        // Ejes propios del producto (generador de variantes): se conservan si la ficha no los envía.
        Set<Long> templateAxisIds = template == null ? Set.of()
                : template.axes().stream().map(TemplateAttribute::attributeId).collect(Collectors.toSet());
        Set<Long> productAxes = store.productAxes(productId).stream()
                .filter(id -> !templateAxisIds.contains(id)).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!productAxes.isEmpty()) {
            Map<Long, Map<Long, Long>> current = store.skuOptions(skuIds);
            Map<Long, VariantSelection> bySku = new LinkedHashMap<>();
            selections.forEach(selection -> bySku.put(selection.skuId(), selection));
            for (Long skuId : skuIds) {
                Map<Long, Long> merged = new LinkedHashMap<>();
                current.getOrDefault(skuId, Map.of()).forEach((attributeId, optionId) -> {
                    if (productAxes.contains(attributeId)) {
                        merged.put(attributeId, optionId);
                    }
                });
                VariantSelection given = bySku.get(skuId);
                if (given != null) {
                    merged.putAll(given.optionsByAttribute());
                }
                if (!merged.isEmpty() || given != null) {
                    bySku.put(skuId, new VariantSelection(skuId, merged));
                }
            }
            selections = new ArrayList<>(bySku.values());
            definitions = new java.util.HashMap<>(definitions);
            definitions.putAll(soporte.definitionsOf(productAxes));
        }
        ProductAttributesPolicy.Result result = ProductAttributesPolicy.check(template, productAxes, definitions, values,
                selections, skuIds, ACTIVE.equals(header.status()));

        store.setTemplate(productId, input.templateId());
        store.replaceValues(productId, result.values());
        store.replaceSkuOptions(skuIds, result.variants());
        return toDto(soporte.load(productId));
    }

    private ProductAttributesDto toDto(Snapshot snapshot) {
        ProductTemplateDto template = snapshot.template() == null ? null
                : templateService.toDtos(List.of(snapshot.template())).get(0);
        Set<Long> templateIds = snapshot.template() == null ? null
                : snapshot.template().getAttributes().stream().map(TemplateAttribute::attributeId).collect(Collectors.toSet());
        Set<Long> axisIds = new LinkedHashSet<>(snapshot.productAxes());
        if (snapshot.template() != null) {
            snapshot.template().axes().forEach(axis -> axisIds.add(axis.attributeId()));
        }
        // Valores de la ficha: los de la plantilla y los adicionales; un eje de la plantilla no
        // va en la ficha (pudo quedar de una plantilla anterior).
        List<AttributeValue> sheetValues = snapshot.values().stream()
                .filter(value -> !axisIds.contains(value.attributeId()))
                .toList();
        List<Attribute> available;
        if (snapshot.template() != null) {
            Set<Long> ids = new LinkedHashSet<>(templateIds.size() + sheetValues.size());
            snapshot.template().getAttributes().forEach(item -> ids.add(item.attributeId()));
            sheetValues.forEach(value -> ids.add(value.attributeId()));
            available = ids.stream()
                    .map(id -> snapshot.definitions().get(id))
                    .filter(java.util.Objects::nonNull)
                    .toList();
        } else {
            available = List.copyOf(snapshot.definitions().values());
        }
        List<AttributeValueDto> values = sheetValues.stream()
                .map(value -> new AttributeValueDto(value.attributeId(), value.text(), value.number(), value.bool(),
                        value.optionId()))
                .toList();
        Map<Long, VariantSelection> bySku = snapshot.selections().stream()
                .collect(Collectors.toMap(VariantSelection::skuId, Function.identity()));
        List<VariantAttributesDto> variants = snapshot.skus().stream()
                .map(sku -> new VariantAttributesDto(sku.id(), sku.code(), sku.name(), sku.active(),
                        bySku.get(sku.id()).optionsByAttribute().entrySet().stream()
                                .filter(entry -> (templateIds != null && templateIds.contains(entry.getKey()))
                                        || snapshot.productAxes().contains(entry.getKey()))
                                .map(entry -> new VariantOptionDto(entry.getKey(), entry.getValue()))
                                .toList()))
                .toList();
        return new ProductAttributesDto(snapshot.header().id(), snapshot.header().status(), snapshot.header().templateId(),
                template, attributeService.toDtos(available), values, variants, snapshot.missing());
    }
}

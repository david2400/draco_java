package com.essenza.draco.modules.catalog.application.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeValueDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.ProductAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.ProductTemplateDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveProductAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.VariantAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.VariantOptionDto;
import com.essenza.draco.modules.catalog.application.input.attribute.ProductAttributesUseCase;
import com.essenza.draco.modules.catalog.application.input.attribute.ProductPublicationGuard;
import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.ProductHeader;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.VariantSku;
import com.essenza.draco.modules.catalog.application.output.repository.ProductTemplateRepository;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeRuleViolation;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeValue;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductAttributesPolicy;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductTemplate;
import com.essenza.draco.modules.catalog.domain.model.attribute.TemplateAttribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.VariantSelection;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
@Transactional
public class ProductAttributesServiceImpl implements ProductAttributesUseCase, ProductPublicationGuard {

    private static final String ACTIVE = "ACTIVE";

    private final ProductAttributesStore store;
    private final ProductTemplateRepository templates;
    private final AttributeRepository attributes;
    private final AttributeServiceImpl attributeService;
    private final ProductTemplateServiceImpl templateService;

    public ProductAttributesServiceImpl(ProductAttributesStore store, ProductTemplateRepository templates,
                                        AttributeRepository attributes, AttributeServiceImpl attributeService,
                                        ProductTemplateServiceImpl templateService) {
        this.store = store;
        this.templates = templates;
        this.attributes = attributes;
        this.attributeService = attributeService;
        this.templateService = templateService;
    }

    /** Estado actual del producto, ya cargado. */
    private record Snapshot(ProductHeader header, ProductTemplate template, Map<Long, Attribute> definitions,
                            List<AttributeValue> values, List<VariantSku> skus, List<VariantSelection> selections) {

        Set<Long> skuIds() {
            return skus.stream().map(VariantSku::id).collect(Collectors.toCollection(LinkedHashSet::new));
        }

        List<String> missing() {
            return ProductAttributesPolicy.missing(template, definitions, values, selections, skuIds());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAttributesDto get(Long productId) {
        return toDto(load(productId));
    }

    @Override
    public ProductAttributesDto save(Long productId, SaveProductAttributesDto input) {
        ProductHeader header = header(productId);
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
        Map<Long, Attribute> definitions = definitionsOf(definitionIds);

        List<VariantSku> skus = store.variantSkus(productId);
        Set<Long> skuIds = skus.stream().map(VariantSku::id).collect(Collectors.toCollection(LinkedHashSet::new));
        ProductAttributesPolicy.Result result = ProductAttributesPolicy.check(template, definitions, values, selections,
                skuIds, ACTIVE.equals(header.status()));

        store.setTemplate(productId, input.templateId());
        store.replaceValues(productId, result.values());
        store.replaceSkuOptions(skuIds, result.variants());
        return toDto(load(productId));
    }

    @Override
    @Transactional(readOnly = true)
    public void assertPublishable(Long productId) {
        if (store.findProduct(productId).isEmpty()) {
            return;
        }
        List<String> missing = load(productId).missing();
        if (!missing.isEmpty()) {
            throw new ConflictException("No se puede publicar el producto: falta " + String.join("; ", missing)
                    + ". Complétalo en la ficha técnica.");
        }
    }

    private ProductHeader header(Long productId) {
        return store.findProduct(productId)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + productId));
    }

    private Snapshot load(Long productId) {
        ProductHeader header = header(productId);
        ProductTemplate template = header.templateId() == null ? null
                : templates.findById(header.templateId()).orElse(null);
        List<AttributeValue> values = store.values(productId);
        List<VariantSku> skus = store.variantSkus(productId);
        Map<Long, Map<Long, Long>> options = store.skuOptions(skus.stream().map(VariantSku::id).toList());
        List<VariantSelection> selections = skus.stream()
                .map(sku -> new VariantSelection(sku.id(), options.getOrDefault(sku.id(), Map.of())))
                .toList();
        Map<Long, Attribute> definitions;
        if (template != null) {
            definitions = definitionsOf(template.getAttributes().stream().map(TemplateAttribute::attributeId).toList());
        } else {
            definitions = attributes.findAll().stream().collect(Collectors.toMap(Attribute::getId, Function.identity(),
                    (left, right) -> left, LinkedHashMap::new));
        }
        return new Snapshot(header, template, definitions, values, skus, selections);
    }

    private Map<Long, Attribute> definitionsOf(java.util.Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return attributes.findByIds(ids).stream().collect(Collectors.toMap(Attribute::getId, Function.identity()));
    }

    private ProductAttributesDto toDto(Snapshot snapshot) {
        ProductTemplateDto template = snapshot.template() == null ? null
                : templateService.toDtos(List.of(snapshot.template())).get(0);
        List<Attribute> available;
        if (snapshot.template() != null) {
            available = snapshot.template().getAttributes().stream()
                    .map(item -> snapshot.definitions().get(item.attributeId()))
                    .filter(java.util.Objects::nonNull)
                    .toList();
        } else {
            available = List.copyOf(snapshot.definitions().values());
        }
        Set<Long> templateIds = snapshot.template() == null ? null
                : snapshot.template().getAttributes().stream().map(TemplateAttribute::attributeId).collect(Collectors.toSet());
        List<AttributeValueDto> values = snapshot.values().stream()
                // Con plantilla solo se muestran sus atributos (los demás quedaron de una plantilla anterior).
                .filter(value -> templateIds == null || templateIds.contains(value.attributeId()))
                .map(value -> new AttributeValueDto(value.attributeId(), value.text(), value.number(), value.bool(),
                        value.optionId()))
                .toList();
        Map<Long, VariantSelection> bySku = snapshot.selections().stream()
                .collect(Collectors.toMap(VariantSelection::skuId, Function.identity()));
        List<VariantAttributesDto> variants = snapshot.skus().stream()
                .map(sku -> new VariantAttributesDto(sku.id(), sku.code(), sku.name(), sku.active(),
                        bySku.get(sku.id()).optionsByAttribute().entrySet().stream()
                                .filter(entry -> templateIds != null && templateIds.contains(entry.getKey()))
                                .map(entry -> new VariantOptionDto(entry.getKey(), entry.getValue()))
                                .toList()))
                .toList();
        return new ProductAttributesDto(snapshot.header().id(), snapshot.header().status(), snapshot.header().templateId(),
                template, attributeService.toDtos(available), values, variants, snapshot.missing());
    }
}

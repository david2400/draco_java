package com.essenza.draco.modules.catalog.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.ProductTemplateDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveProductTemplateDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.TemplateAttributeDto;
import com.essenza.draco.modules.catalog.application.input.attribute.ManageProductTemplatesUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductTemplateRepository;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductTemplate;
import com.essenza.draco.modules.catalog.domain.model.attribute.TemplateAttribute;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
@Transactional
public class ProductTemplateServiceImpl implements ManageProductTemplatesUseCase {

    private final ProductTemplateRepository templates;
    private final AttributeRepository attributes;
    private final AttributeServiceImpl attributeService;

    public ProductTemplateServiceImpl(ProductTemplateRepository templates, AttributeRepository attributes,
                                      AttributeServiceImpl attributeService) {
        this.templates = templates;
        this.attributes = attributes;
        this.attributeService = attributeService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductTemplateDto> findAll() {
        return toDtos(templates.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductTemplateDto> findById(Long id) {
        return templates.findById(id).map(template -> toDtos(List.of(template)).get(0));
    }

    @Override
    public ProductTemplateDto create(SaveProductTemplateDto input) {
        ProductTemplate template = toDomain(null, input);
        validate(template, null);
        return toDtos(List.of(templates.save(template))).get(0);
    }

    @Override
    public ProductTemplateDto update(Long id, SaveProductTemplateDto input) {
        templates.findById(id).orElseThrow(() -> new NotFoundException("Plantilla no encontrada: " + id));
        ProductTemplate template = toDomain(id, input);
        validate(template, id);
        return toDtos(List.of(templates.save(template))).get(0);
    }

    @Override
    public void delete(Long id) {
        ProductTemplate current = templates.findById(id)
                .orElseThrow(() -> new NotFoundException("Plantilla no encontrada: " + id));
        long products = templates.productCounts().getOrDefault(id, 0L);
        if (products > 0) {
            throw new ConflictException("\"" + current.getName() + "\" la usan " + products
                    + (products == 1 ? " producto" : " productos") + ": cámbiales la plantilla antes de borrarla.");
        }
        templates.deleteById(id);
    }

    private void validate(ProductTemplate template, Long excludeId) {
        if (templates.existsActiveName(template.getName(), excludeId)) {
            throw new ConflictException("Ya existe una plantilla llamada \"" + template.getName() + "\".");
        }
        Set<Long> ids = template.getAttributes().stream().map(TemplateAttribute::attributeId).collect(Collectors.toSet());
        Map<Long, Attribute> definitions = attributes.findByIds(ids).stream()
                .collect(Collectors.toMap(Attribute::getId, Function.identity()));
        template.validateAgainst(definitions);
    }

    private static ProductTemplate toDomain(Long id, SaveProductTemplateDto input) {
        List<TemplateAttribute> items = new ArrayList<>();
        List<TemplateAttributeDto> source = input.attributes() == null ? List.of() : input.attributes();
        for (int index = 0; index < source.size(); index++) {
            TemplateAttributeDto item = source.get(index);
            items.add(new TemplateAttribute(item.attributeId(), Boolean.TRUE.equals(item.required()),
                    Boolean.TRUE.equals(item.variantAxis()), Boolean.TRUE.equals(item.filterable()),
                    item.position() != null ? item.position() : index));
        }
        return new ProductTemplate(id, input.name(), input.description(), items);
    }

    public List<ProductTemplateDto> toDtos(List<ProductTemplate> list) {
        Set<Long> attributeIds = list.stream()
                .flatMap(template -> template.getAttributes().stream().map(TemplateAttribute::attributeId))
                .collect(Collectors.toSet());
        Map<Long, AttributeDto> definitions = attributeIds.isEmpty() ? Map.of()
                : attributeService.toDtos(attributes.findByIds(attributeIds)).stream()
                        .collect(Collectors.toMap(AttributeDto::id, Function.identity()));
        Map<Long, Long> counts = list.isEmpty() ? Map.of() : templates.productCounts();
        return list.stream().map(template -> new ProductTemplateDto(
                template.getId(), template.getName(), template.getDescription(),
                template.getAttributes().stream().map(item -> new TemplateAttributeDto(item.attributeId(),
                        item.required(), item.variantAxis(), item.filterable(), item.position(),
                        definitions.get(item.attributeId()))).toList(),
                counts.getOrDefault(template.getId(), 0L))).toList();
    }
}

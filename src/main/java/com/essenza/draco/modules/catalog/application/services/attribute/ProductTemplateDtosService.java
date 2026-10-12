package com.essenza.draco.modules.catalog.application.services.attribute;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.ProductTemplateDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.TemplateAttributeDto;
import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductTemplateRepository;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductTemplate;
import com.essenza.draco.modules.catalog.domain.model.attribute.TemplateAttribute;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductTemplateDtosService {

    private final ProductTemplateRepository templates;
    private final AttributeRepository attributes;
    private final AttributeDtosService attributeService;

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

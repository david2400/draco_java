package com.essenza.draco.modules.catalog.application.services.attribute;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeOptionDto;
import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;

@Service
@Transactional
@RequiredArgsConstructor
public class AttributeDtosService {

    private final AttributeRepository attributes;

    public List<AttributeDto> toDtos(List<Attribute> list) {
        Set<Long> ids = list.stream().map(Attribute::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> used = ids.isEmpty() ? Set.of() : attributes.inUse(ids);
        Set<Long> unitIds = list.stream().map(Attribute::getUnitId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> units = unitIds.isEmpty() ? Map.of() : attributes.unitNames(unitIds);
        // Map.of()/Set.of() no admiten null en get/contains: un atributo sin unidad tiene unit_id nulo.
        return list.stream().map(attribute -> toDto(attribute,
                attribute.getId() != null && used.contains(attribute.getId()),
                attribute.getUnitId() == null ? null : units.get(attribute.getUnitId()))).toList();
    }

    static AttributeDto toDto(Attribute attribute, boolean inUse, String unitName) {
        return new AttributeDto(attribute.getId(), attribute.getCode(), attribute.getName(), attribute.getDescription(),
                attribute.getDataType().name(), attribute.getUnitId(), unitName,
                attribute.getOptions().stream()
                        .map(option -> new AttributeOptionDto(option.id(), option.value(), option.position()))
                        .toList(),
                inUse);
    }
}

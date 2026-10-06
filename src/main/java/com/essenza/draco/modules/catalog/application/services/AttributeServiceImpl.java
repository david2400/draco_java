package com.essenza.draco.modules.catalog.application.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeOptionDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveAttributeDto;
import com.essenza.draco.modules.catalog.application.input.attribute.ManageAttributesUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeDataType;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeOption;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeRuleViolation;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
@Transactional
public class AttributeServiceImpl implements ManageAttributesUseCase {

    private final AttributeRepository attributes;

    public AttributeServiceImpl(AttributeRepository attributes) {
        this.attributes = attributes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeDto> findAll() {
        return toDtos(attributes.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttributeDto> findById(Long id) {
        return attributes.findById(id).map(attribute -> toDtos(List.of(attribute)).get(0));
    }

    @Override
    public AttributeDto create(SaveAttributeDto input) {
        Attribute attribute = toDomain(null, input);
        checkCodeAndUnit(attribute, null);
        return toDtos(List.of(attributes.save(attribute))).get(0);
    }

    @Override
    public AttributeDto update(Long id, SaveAttributeDto input) {
        Attribute current = attributes.findById(id)
                .orElseThrow(() -> new NotFoundException("Atributo no encontrado: " + id));
        Attribute attribute = toDomain(id, input);
        checkCodeAndUnit(attribute, id);

        boolean used = !attributes.inUse(Set.of(id)).isEmpty();
        if (used && current.getDataType() != attribute.getDataType()) {
            throw new ConflictException("No se puede cambiar el tipo de \"" + current.getName()
                    + "\": ya se usa en plantillas o productos.");
        }
        Set<Long> currentOptionIds = current.getOptions().stream().map(AttributeOption::id).collect(Collectors.toSet());
        Set<Long> keptOptionIds = new HashSet<>();
        for (AttributeOption option : attribute.getOptions()) {
            if (option.id() == null) {
                continue;
            }
            if (!currentOptionIds.contains(option.id())) {
                throw new AttributeRuleViolation("La opción " + option.id() + " no pertenece al atributo.");
            }
            keptOptionIds.add(option.id());
        }
        Set<Long> removed = new HashSet<>(currentOptionIds);
        removed.removeAll(keptOptionIds);
        Set<Long> removedInUse = attributes.optionsInUse(removed);
        if (!removedInUse.isEmpty()) {
            String values = current.getOptions().stream()
                    .filter(option -> removedInUse.contains(option.id()))
                    .map(AttributeOption::value)
                    .collect(Collectors.joining("\", \""));
            throw new ConflictException("No se puede quitar \"" + values + "\": hay productos o variantes que la usan.");
        }
        return toDtos(List.of(attributes.save(attribute))).get(0);
    }

    @Override
    public void delete(Long id) {
        Attribute current = attributes.findById(id)
                .orElseThrow(() -> new NotFoundException("Atributo no encontrado: " + id));
        if (!attributes.inUse(Set.of(id)).isEmpty()) {
            throw new ConflictException("\"" + current.getName() + "\" se usa en plantillas o productos: quítalo de ellos antes de borrarlo.");
        }
        attributes.deleteById(id);
    }

    private void checkCodeAndUnit(Attribute attribute, Long excludeId) {
        if (attributes.existsActiveCode(attribute.getCode(), excludeId)) {
            throw new ConflictException("Ya existe un atributo con el código \"" + attribute.getCode() + "\".");
        }
        if (attribute.getUnitId() != null && !attributes.unitExists(attribute.getUnitId())) {
            throw new AttributeRuleViolation("La unidad " + attribute.getUnitId() + " no existe.");
        }
    }

    private static Attribute toDomain(Long id, SaveAttributeDto input) {
        AttributeDataType type = AttributeDataType.parse(input.dataType());
        List<AttributeOption> options = new ArrayList<>();
        List<AttributeOptionDto> source = input.options() == null ? List.of() : input.options();
        for (int index = 0; index < source.size(); index++) {
            AttributeOptionDto option = source.get(index);
            int position = option.position() != null ? option.position() : index;
            options.add(new AttributeOption(option.id(), option.value(), position));
        }
        return new Attribute(id, input.code(), input.name(), input.description(), type, input.unitId(),
                type == AttributeDataType.OPTION ? options : List.of());
    }

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

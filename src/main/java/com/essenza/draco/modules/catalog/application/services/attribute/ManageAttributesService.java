package com.essenza.draco.modules.catalog.application.services.attribute;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeOptionDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeUsageDto;
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
@RequiredArgsConstructor
public class ManageAttributesService implements ManageAttributesUseCase {

    private final AttributeRepository attributes;
    private final AttributeDtosService attributeService;

    @Override
    @Transactional(readOnly = true)
    public List<AttributeDto> findAll() {
        return attributeService.toDtos(attributes.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttributeDto> findById(Long id) {
        return attributes.findById(id).map(attribute -> attributeService.toDtos(List.of(attribute)).get(0));
    }

    @Override
    public AttributeDto create(SaveAttributeDto input) {
        Attribute attribute = toDomain(null, input);
        checkCodeAndUnit(attribute, null);
        return attributeService.toDtos(List.of(attributes.save(attribute))).get(0);
    }

    @Override
    public AttributeDto update(Long id, SaveAttributeDto input) {
        Attribute current = attributes.findById(id)
                .orElseThrow(() -> new NotFoundException("Atributo no encontrado: " + id));
        Attribute attribute = toDomain(id, input);
        checkCodeAndUnit(attribute, id);

        boolean used = !attributes.inUse(Set.of(id)).isEmpty();
        BigDecimal rescale = unitChange(current, attribute);
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
        Attribute saved = attributes.save(attribute);
        if (rescale != null) {
            attributes.rescaleNumberValues(id, rescale);
        }
        return attributeService.toDtos(List.of(saved)).get(0);
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

    @Override
    @Transactional(readOnly = true)
    public AttributeUsageDto usage(Long id) {
        attributes.findById(id).orElseThrow(() -> new NotFoundException("Atributo no encontrado: " + id));
        return attributes.usage(id);
    }

    private void checkCodeAndUnit(Attribute attribute, Long excludeId) {
        if (attributes.existsActiveCode(attribute.getCode(), excludeId)) {
            throw new ConflictException("Ya existe un atributo con el código \"" + attribute.getCode() + "\".");
        }
        if (attribute.getUnitId() != null && !attributes.unitExists(attribute.getUnitId())) {
            throw new AttributeRuleViolation("La unidad " + attribute.getUnitId() + " no existe.");
        }
    }

    /**
     * Cambio de unidad de un atributo numérico con valores guardados: dentro de la misma
     * magnitud devuelve el multiplicador para convertirlos (ml → L: 0.001); a otra magnitud,
     * o quitando/poniendo unidad, es un conflicto porque los números cambiarían de sentido.
     * {@code null} si no hay nada que convertir.
     */
    private BigDecimal unitChange(Attribute current, Attribute next) {
        if (current.getDataType() != AttributeDataType.NUMBER || next.getDataType() != AttributeDataType.NUMBER
                || Objects.equals(current.getUnitId(), next.getUnitId())) {
            return null;
        }
        long values = attributes.countNumberValues(current.getId());
        if (values == 0) {
            return null;
        }
        Optional<AttributeRepository.UnitInfo> from = current.getUnitId() == null ? Optional.empty() : attributes.unit(current.getUnitId());
        Optional<AttributeRepository.UnitInfo> to = next.getUnitId() == null ? Optional.empty() : attributes.unit(next.getUnitId());
        if (from.isEmpty() || to.isEmpty() || !from.get().dimension().equals(to.get().dimension())
                || "OTHER".equals(from.get().dimension())) {
            throw new ConflictException("No se puede cambiar la unidad de \"" + current.getName() + "\" a otra magnitud: "
                    + values + " producto(s) tienen un valor guardado. Elige una unidad de la misma magnitud.");
        }
        return from.get().factor().divide(to.get().factor(), new MathContext(20, RoundingMode.HALF_UP));
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
        // La unidad solo tiene sentido en atributos numéricos (500 ml, 30 cm…).
        Long unitId = type == AttributeDataType.NUMBER ? input.unitId() : null;
        return new Attribute(id, input.code(), input.name(), input.description(), type, unitId,
                type == AttributeDataType.OPTION ? options : List.of());
    }
}

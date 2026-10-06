package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.attribute;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeDataType;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeOption;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.AttributeEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.AttributeOptionEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;

import jakarta.persistence.EntityManager;

@Repository
public class AttributeRepositoryAdapter implements AttributeRepository {

    private final JpaAttributeRepository jpa;
    private final EntityManager em;

    public AttributeRepositoryAdapter(JpaAttributeRepository jpa, EntityManager em) {
        this.jpa = jpa;
        this.em = em;
    }

    @Override
    public List<Attribute> findAll() {
        return jpa.findAllWithOptions().stream().map(AttributeRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<Attribute> findById(Long id) {
        return jpa.findById(id).map(AttributeRepositoryAdapter::toDomain);
    }

    @Override
    public List<Attribute> findByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpa.findAllWithOptionsByIdIn(ids).stream().map(AttributeRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Attribute save(Attribute attribute) {
        AttributeEntity entity = attribute.getId() == null ? new AttributeEntity()
                : jpa.findById(attribute.getId())
                        .orElseThrow(() -> new NotFoundException("Atributo no encontrado: " + attribute.getId()));
        entity.setCode(attribute.getCode());
        entity.setName(attribute.getName());
        entity.setDescription(attribute.getDescription());
        entity.setDataType(attribute.getDataType().name());
        entity.setUnitId(attribute.getUnitId());
        mergeOptions(entity, attribute.getOptions());
        AttributeEntity saved = jpa.saveAndFlush(entity);
        return toDomain(saved);
    }

    /** Actualiza en sitio las opciones con id, crea las nuevas y quita las ausentes. */
    private static void mergeOptions(AttributeEntity entity, List<AttributeOption> options) {
        Map<Long, AttributeOption> incoming = new HashMap<>();
        options.stream().filter(option -> option.id() != null).forEach(option -> incoming.put(option.id(), option));
        Iterator<AttributeOptionEntity> iterator = entity.getOptions().iterator();
        while (iterator.hasNext()) {
            AttributeOptionEntity current = iterator.next();
            AttributeOption update = incoming.get(current.getId());
            if (update == null) {
                iterator.remove();
            } else {
                current.setValue(update.value());
                current.setPosition(update.position());
            }
        }
        for (AttributeOption option : options) {
            if (option.id() == null) {
                AttributeOptionEntity created = new AttributeOptionEntity();
                created.setAttribute(entity);
                created.setValue(option.value());
                created.setPosition(option.position());
                entity.getOptions().add(created);
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        jpa.findById(id).ifPresent(entity -> {
            // Las opciones se borran físicamente; el atributo, de forma lógica.
            entity.getOptions().clear();
            jpa.saveAndFlush(entity);
            jpa.delete(entity);
        });
    }

    @Override
    public boolean existsActiveCode(String code, Long excludeId) {
        return excludeId == null ? jpa.existsByCodeIgnoreCase(code) : jpa.existsByCodeIgnoreCaseAndIdNot(code, excludeId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Long> inUse(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        List<Number> rows = em.createNativeQuery("""
                SELECT ta.attribute_id FROM template_attributes ta
                  JOIN product_templates pt ON pt.id_template = ta.template_id AND pt.deleted = 0
                 WHERE ta.attribute_id IN (:ids)
                UNION SELECT attribute_id FROM product_attribute_values WHERE attribute_id IN (:ids)
                UNION SELECT attribute_id FROM sku_attribute_values WHERE attribute_id IN (:ids)
                """).setParameter("ids", ids).getResultList();
        return rows.stream().map(Number::longValue).collect(Collectors.toSet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Long> optionsInUse(Collection<Long> optionIds) {
        if (optionIds == null || optionIds.isEmpty()) {
            return Set.of();
        }
        List<Number> rows = em.createNativeQuery("""
                SELECT option_id FROM product_attribute_values WHERE option_id IN (:ids)
                UNION SELECT option_id FROM sku_attribute_values WHERE option_id IN (:ids)
                """).setParameter("ids", optionIds).getResultList();
        return rows.stream().map(Number::longValue).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public boolean unitExists(Long unitId) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM units_measurement WHERE id_unit_measurement = :id AND deleted = 0")
                .setParameter("id", unitId).getSingleResult();
        return count.longValue() > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, String> unitNames(Collection<Long> unitIds) {
        if (unitIds == null || unitIds.isEmpty()) {
            return Map.of();
        }
        List<Object[]> rows = em.createNativeQuery(
                "SELECT id_unit_measurement, name FROM units_measurement WHERE id_unit_measurement IN (:ids)")
                .setParameter("ids", unitIds).getResultList();
        Map<Long, String> names = new HashMap<>();
        rows.forEach(row -> names.put(((Number) row[0]).longValue(), (String) row[1]));
        return names;
    }

    static Attribute toDomain(AttributeEntity entity) {
        AttributeDataType type = AttributeDataType.valueOf(entity.getDataType());
        List<AttributeOption> options = entity.getOptions().stream()
                .sorted(Comparator.comparingInt(AttributeOptionEntity::getPosition)
                        .thenComparing(option -> option.getId() == null ? Long.MAX_VALUE : option.getId()))
                .map(option -> new AttributeOption(option.getId(), option.getValue(), option.getPosition()))
                .toList();
        return new Attribute(entity.getId(), entity.getCode(), entity.getName(), entity.getDescription(), type,
                entity.getUnitId(), type == AttributeDataType.OPTION ? options : List.of());
    }
}

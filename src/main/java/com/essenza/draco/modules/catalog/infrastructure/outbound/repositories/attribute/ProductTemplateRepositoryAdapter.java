package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.attribute;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.catalog.application.output.repository.ProductTemplateRepository;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductTemplate;
import com.essenza.draco.modules.catalog.domain.model.attribute.TemplateAttribute;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductTemplateEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.TemplateAttributeEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;

import jakarta.persistence.EntityManager;

@Repository
public class ProductTemplateRepositoryAdapter implements ProductTemplateRepository {

    private final JpaProductTemplateRepository jpa;
    private final EntityManager em;

    public ProductTemplateRepositoryAdapter(JpaProductTemplateRepository jpa, EntityManager em) {
        this.jpa = jpa;
        this.em = em;
    }

    @Override
    public List<ProductTemplate> findAll() {
        return jpa.findAllWithAttributes().stream().map(ProductTemplateRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<ProductTemplate> findById(Long id) {
        return jpa.findById(id).map(ProductTemplateRepositoryAdapter::toDomain);
    }

    @Override
    public ProductTemplate save(ProductTemplate template) {
        ProductTemplateEntity entity = template.getId() == null ? new ProductTemplateEntity()
                : jpa.findById(template.getId())
                        .orElseThrow(() -> new NotFoundException("Plantilla no encontrada: " + template.getId()));
        entity.setName(template.getName());
        entity.setDescription(template.getDescription());
        if (entity.getId() == null) {
            // Primero la cabecera: los atributos necesitan su id en la clave compuesta.
            entity = jpa.saveAndFlush(entity);
        }
        mergeAttributes(entity, template.getAttributes());
        return toDomain(jpa.saveAndFlush(entity));
    }

    /**
     * Actualiza en sitio los atributos que siguen, quita los ausentes y agrega los
     * nuevos (sin borrar y reinsertar la misma clave en un mismo flush).
     */
    private static void mergeAttributes(ProductTemplateEntity entity, List<TemplateAttribute> attributes) {
        Map<Long, TemplateAttribute> incoming = attributes.stream()
                .collect(Collectors.toMap(TemplateAttribute::attributeId, Function.identity()));
        Iterator<TemplateAttributeEntity> iterator = entity.getAttributes().iterator();
        while (iterator.hasNext()) {
            TemplateAttributeEntity current = iterator.next();
            TemplateAttribute update = incoming.remove(current.getId().getAttributeId());
            if (update == null) {
                iterator.remove();
            } else {
                apply(current, update);
            }
        }
        for (TemplateAttribute item : attributes) {
            if (incoming.containsKey(item.attributeId())) {
                TemplateAttributeEntity created = new TemplateAttributeEntity();
                created.setId(new TemplateAttributeEntity.Key(entity.getId(), item.attributeId()));
                created.setTemplate(entity);
                apply(created, item);
                entity.getAttributes().add(created);
            }
        }
    }

    private static void apply(TemplateAttributeEntity target, TemplateAttribute source) {
        target.setRequired(source.required());
        target.setVariantAxis(source.variantAxis());
        target.setFilterable(source.filterable());
        target.setPosition(source.position());
    }

    @Override
    public void deleteById(Long id) {
        jpa.findById(id).ifPresent(entity -> {
            entity.getAttributes().clear();
            jpa.saveAndFlush(entity);
            jpa.delete(entity);
        });
    }

    @Override
    public boolean existsActiveName(String name, Long excludeId) {
        return excludeId == null ? jpa.existsByNameIgnoreCase(name) : jpa.existsByNameIgnoreCaseAndIdNot(name, excludeId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Long> productCounts() {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT template_id, COUNT(*) FROM products
                 WHERE template_id IS NOT NULL AND deleted = 0
                 GROUP BY template_id
                """).getResultList();
        Map<Long, Long> counts = new HashMap<>();
        rows.forEach(row -> counts.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue()));
        return counts;
    }

    static ProductTemplate toDomain(ProductTemplateEntity entity) {
        List<TemplateAttribute> attributes = entity.getAttributes().stream()
                .map(item -> new TemplateAttribute(item.getId().getAttributeId(), item.isRequired(), item.isVariantAxis(),
                        item.isFilterable(), item.getPosition()))
                .toList();
        return new ProductTemplate(entity.getId(), entity.getName(), entity.getDescription(), attributes);
    }
}

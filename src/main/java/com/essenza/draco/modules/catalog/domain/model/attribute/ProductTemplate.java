package com.essenza.draco.modules.catalog.domain.model.attribute;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Plantilla por tipo de producto: qué atributos lleva y cuáles definen variantes. */
public final class ProductTemplate {

    private final Long id;
    private final String name;
    private final String description;
    private final List<TemplateAttribute> attributes;

    public ProductTemplate(Long id, String name, String description, List<TemplateAttribute> attributes) {
        this.id = id;
        this.name = name == null ? null : name.trim();
        this.description = description == null || description.isBlank() ? null : description.trim();
        this.attributes = attributes == null ? List.of()
                : attributes.stream().sorted(Comparator.comparingInt(TemplateAttribute::position)).toList();
        if (this.name == null || this.name.isEmpty() || this.name.length() > 120) {
            throw new AttributeRuleViolation("El nombre de la plantilla es obligatorio (máximo 120 caracteres).");
        }
        if (this.description != null && this.description.length() > 500) {
            throw new AttributeRuleViolation("La descripción supera 500 caracteres.");
        }
        Set<Long> seen = new HashSet<>();
        for (TemplateAttribute attribute : this.attributes) {
            if (!seen.add(attribute.attributeId())) {
                throw new AttributeRuleViolation("Un atributo está repetido en la plantilla.");
            }
        }
    }

    /** Comprueba la plantilla contra las definiciones: existen y los ejes son de tipo OPTION. */
    public void validateAgainst(Map<Long, Attribute> definitions) {
        for (TemplateAttribute item : attributes) {
            Attribute definition = definitions.get(item.attributeId());
            if (definition == null) {
                throw new AttributeRuleViolation("El atributo " + item.attributeId() + " no existe.");
            }
            if (item.variantAxis() && !definition.isOption()) {
                throw new AttributeRuleViolation(
                        "\"" + definition.getName() + "\" no puede ser eje de variante: solo los atributos de opciones (OPTION).");
            }
            if (item.variantAxis() && item.required()) {
                // Un eje siempre se exige en cada variante al publicar; "obligatorio" aplica a la ficha.
                throw new AttributeRuleViolation(
                        "\"" + definition.getName() + "\" es eje de variante: no se marca como obligatorio de ficha.");
            }
        }
    }

    public Optional<TemplateAttribute> find(Long attributeId) {
        return attributes.stream().filter(item -> Objects.equals(item.attributeId(), attributeId)).findFirst();
    }

    public List<TemplateAttribute> axes() {
        return attributes.stream().filter(TemplateAttribute::variantAxis).toList();
    }

    public List<TemplateAttribute> sheetAttributes() {
        return attributes.stream().filter(item -> !item.variantAxis()).toList();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<TemplateAttribute> getAttributes() {
        return attributes;
    }
}

package com.essenza.draco.modules.catalog.domain.model.attribute;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Definición de un atributo del catálogo (ficha técnica o eje de variante).
 * Los campos de núcleo del producto (precio, peso, marca…) no son atributos.
 */
public final class Attribute {

    private static final Pattern CODE = Pattern.compile("^[a-z][a-z0-9_]{1,59}$");

    private final Long id;
    private final String code;
    private final String name;
    private final String description;
    private final AttributeDataType dataType;
    private final Long unitId;
    private final List<AttributeOption> options;

    public Attribute(Long id, String code, String name, String description,
                     AttributeDataType dataType, Long unitId, List<AttributeOption> options) {
        this.id = id;
        this.code = code == null ? null : code.trim();
        this.name = name == null ? null : name.trim();
        this.description = description == null || description.isBlank() ? null : description.trim();
        this.dataType = Objects.requireNonNull(dataType, "dataType");
        this.unitId = unitId;
        this.options = options == null ? List.of() : List.copyOf(options);
        validate();
    }

    private void validate() {
        if (code == null || !CODE.matcher(code).matches()) {
            throw new AttributeRuleViolation(
                    "El código debe empezar por letra y usar solo minúsculas, números y \"_\" (2 a 60 caracteres).");
        }
        if (name == null || name.isEmpty() || name.length() > 120) {
            throw new AttributeRuleViolation("El nombre es obligatorio (máximo 120 caracteres).");
        }
        if (description != null && description.length() > 500) {
            throw new AttributeRuleViolation("La descripción supera 500 caracteres.");
        }
        if (dataType == AttributeDataType.OPTION) {
            if (options.isEmpty()) {
                throw new AttributeRuleViolation("Un atributo de opciones necesita al menos una opción.");
            }
            Set<String> seen = new HashSet<>();
            for (AttributeOption option : options) {
                if (!seen.add(option.value().toLowerCase(Locale.ROOT))) {
                    throw new AttributeRuleViolation("La opción \"" + option.value() + "\" está repetida.");
                }
            }
        } else if (!options.isEmpty()) {
            throw new AttributeRuleViolation("Solo los atributos de tipo OPTION tienen opciones.");
        }
    }

    public Optional<AttributeOption> option(Long optionId) {
        return options.stream().filter(option -> Objects.equals(option.id(), optionId)).findFirst();
    }

    public boolean isOption() {
        return dataType == AttributeDataType.OPTION;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public AttributeDataType getDataType() {
        return dataType;
    }

    public Long getUnitId() {
        return unitId;
    }

    public List<AttributeOption> getOptions() {
        return options;
    }
}

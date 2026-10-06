package com.essenza.draco.modules.catalog.application.output.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;

/** Persistencia de atributos y sus opciones. */
public interface AttributeRepository {

    List<Attribute> findAll();

    Optional<Attribute> findById(Long id);

    List<Attribute> findByIds(Collection<Long> ids);

    /** Guarda el atributo; las opciones sin id se crean y las ausentes se eliminan. */
    Attribute save(Attribute attribute);

    void deleteById(Long id);

    boolean existsActiveCode(String code, Long excludeId);

    /** Ids de atributos usados en plantillas o en valores de productos/SKU. */
    Set<Long> inUse(Collection<Long> ids);

    /** Ids de opciones usadas en valores de productos/SKU. */
    Set<Long> optionsInUse(Collection<Long> optionIds);

    boolean unitExists(Long unitId);

    Map<Long, String> unitNames(Collection<Long> unitIds);
}

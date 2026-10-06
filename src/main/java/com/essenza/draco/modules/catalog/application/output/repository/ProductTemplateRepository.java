package com.essenza.draco.modules.catalog.application.output.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.essenza.draco.modules.catalog.domain.model.attribute.ProductTemplate;

/** Persistencia de plantillas de producto. */
public interface ProductTemplateRepository {

    List<ProductTemplate> findAll();

    Optional<ProductTemplate> findById(Long id);

    ProductTemplate save(ProductTemplate template);

    void deleteById(Long id);

    boolean existsActiveName(String name, Long excludeId);

    /** Productos (no borrados) por plantilla. */
    Map<Long, Long> productCounts();
}

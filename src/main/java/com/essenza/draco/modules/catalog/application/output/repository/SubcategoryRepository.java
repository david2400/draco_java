package com.essenza.draco.modules.catalog.application.output.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.essenza.draco.modules.catalog.application.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.application.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.application.dto.subcategory.UpdateSubcategoryDto;

public interface SubcategoryRepository {

    SubcategoryDto create(CreateSubcategoryDto input);

    SubcategoryDto update(Long id, UpdateSubcategoryDto input);

    boolean deleteById(Long id);

    Optional<SubcategoryDto> findById(Long id);

    List<SubcategoryDto> findAll();

    Optional<SubcategoryDto> findByName(String name);

    Page<SubcategoryDto> search(String query, Long categoryId, Pageable pageable);

    boolean existsById(Long id);

    /** ¿Existe otro registro (distinto de {@code excludeId}) con ese nombre? */
    boolean existsByName(String name, Long excludeId);

    /** ¿Existe otro registro (distinto de {@code excludeId}) con ese slug? */
    boolean existsBySlug(String slug, Long excludeId);

    long count();

    long countByCategoryId(Long categoryId);
}

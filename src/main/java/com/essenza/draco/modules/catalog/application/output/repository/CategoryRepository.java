package com.essenza.draco.modules.catalog.application.output.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.essenza.draco.modules.catalog.domain.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.CreateCategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.UpdateCategoryDto;

public interface CategoryRepository {

    CategoryDto create(CreateCategoryDto input);

    CategoryDto update(Long id, UpdateCategoryDto input);

    boolean deleteById(Long id);

    Optional<CategoryDto> findById(Long id);

    List<CategoryDto> findAll();

    Optional<CategoryDto> findByName(String name);

    Page<CategoryDto> search(String query, Pageable pageable);

    boolean existsById(Long id);

    /** ¿Existe otro registro (distinto de {@code excludeId}) con ese nombre? */
    boolean existsByName(String name, Long excludeId);

    /** ¿Existe otro registro (distinto de {@code excludeId}) con ese slug? */
    boolean existsBySlug(String slug, Long excludeId);
}

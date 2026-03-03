package com.essenza.draco.modules.catalog.application.output.repository;

import com.essenza.draco.modules.catalog.domain.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.CreateCategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.UpdateCategoryDto;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    CategoryDto create(CreateCategoryDto input);

    CategoryDto update(Long id, UpdateCategoryDto input);

    boolean deleteById(Long id);

    Optional<CategoryDto> findById(Long id);

    List<CategoryDto> findAll();

    Optional<CategoryDto> findByName(String name);
}

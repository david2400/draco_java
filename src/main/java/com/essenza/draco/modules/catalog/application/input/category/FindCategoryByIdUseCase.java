package com.essenza.draco.modules.catalog.application.input.category;

import com.essenza.draco.modules.catalog.domain.dto.category.CategoryDto;

import java.util.Optional;

public interface FindCategoryByIdUseCase {
    Optional<CategoryDto> findById(Long id);
}

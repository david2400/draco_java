package com.essenza.draco.modules.catalog.application.input.category;

import com.essenza.draco.modules.catalog.domain.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.UpdateCategoryDto;

public interface UpdateCategoryUseCase {
    CategoryDto update(Long id, UpdateCategoryDto input);
}

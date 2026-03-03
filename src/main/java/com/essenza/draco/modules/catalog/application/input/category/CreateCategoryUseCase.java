package com.essenza.draco.modules.catalog.application.input.category;

import com.essenza.draco.modules.catalog.domain.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.CreateCategoryDto;

public interface CreateCategoryUseCase {
    CategoryDto create(CreateCategoryDto input);
}

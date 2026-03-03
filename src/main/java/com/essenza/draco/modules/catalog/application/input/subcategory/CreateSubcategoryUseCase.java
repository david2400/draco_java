package com.essenza.draco.modules.catalog.application.input.subcategory;

import com.essenza.draco.modules.catalog.domain.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;

public interface CreateSubcategoryUseCase {
    SubcategoryDto create(CreateSubcategoryDto input);
}

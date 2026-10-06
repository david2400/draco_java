package com.essenza.draco.modules.catalog.application.input.subcategory;

import com.essenza.draco.modules.catalog.application.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.application.dto.subcategory.SubcategoryDto;

public interface CreateSubcategoryUseCase {
    SubcategoryDto create(CreateSubcategoryDto input);
}

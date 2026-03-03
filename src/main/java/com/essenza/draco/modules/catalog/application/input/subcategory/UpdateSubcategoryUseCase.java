package com.essenza.draco.modules.catalog.application.input.subcategory;

import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.UpdateSubcategoryDto;

public interface UpdateSubcategoryUseCase {
    SubcategoryDto update(Long id, UpdateSubcategoryDto input);
}

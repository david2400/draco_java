package com.essenza.draco.modules.catalog.application.input.subcategory;

import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;

import java.util.Optional;

public interface FindSubcategoryByIdUseCase {
    Optional<SubcategoryDto> findById(Long id);
}

package com.essenza.draco.modules.catalog.application.input.subcategory;

import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;

import java.util.List;

public interface FindAllSubcategoriesUseCase {
    List<SubcategoryDto> findAll();
}

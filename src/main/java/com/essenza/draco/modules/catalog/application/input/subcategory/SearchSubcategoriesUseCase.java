package com.essenza.draco.modules.catalog.application.input.subcategory;

import org.springframework.data.domain.Pageable;

import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;

/** Búsqueda paginada con texto libre (nombre, slug o descripción). */
public interface SearchSubcategoriesUseCase {
    PageResponse<SubcategoryDto> search(String query, Long categoryId, Pageable pageable);
}

package com.essenza.draco.modules.catalog.application.input.category;

import org.springframework.data.domain.Pageable;

import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.modules.catalog.application.dto.category.CategoryDto;

/** Búsqueda paginada con texto libre (nombre, slug o descripción). */
public interface SearchCategoriesUseCase {
    PageResponse<CategoryDto> search(String query, Pageable pageable);
}

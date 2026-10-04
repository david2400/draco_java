package com.essenza.draco.modules.catalog.application.input.brand;

import org.springframework.data.domain.Pageable;

import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;

/** Búsqueda paginada con texto libre (nombre, slug o descripción). */
public interface SearchBrandsUseCase {
    PageResponse<BrandDto> search(String query, Pageable pageable);
}

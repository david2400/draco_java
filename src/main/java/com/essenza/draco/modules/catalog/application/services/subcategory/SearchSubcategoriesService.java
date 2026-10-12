package com.essenza.draco.modules.catalog.application.services.subcategory;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.application.input.subcategory.SearchSubcategoriesUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.SubcategoryRepository;
import com.essenza.draco.shared.common.domain.dto.PageResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchSubcategoriesService implements SearchSubcategoriesUseCase {

    private final SubcategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SubcategoryDto> search(String query, Long categoryId, Pageable pageable) {
        return PageResponse.from(repository.search(query, categoryId, pageable));
    }
}

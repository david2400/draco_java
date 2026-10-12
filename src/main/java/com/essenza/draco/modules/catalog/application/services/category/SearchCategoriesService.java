package com.essenza.draco.modules.catalog.application.services.category;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.application.input.category.SearchCategoriesUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.CategoryRepository;
import com.essenza.draco.shared.common.domain.dto.PageResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchCategoriesService implements SearchCategoriesUseCase {

    private final CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryDto> search(String query, Pageable pageable) {
        return PageResponse.from(repository.search(query, pageable));
    }
}

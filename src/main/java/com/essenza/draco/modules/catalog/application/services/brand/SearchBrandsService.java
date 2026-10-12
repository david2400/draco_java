package com.essenza.draco.modules.catalog.application.services.brand;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.application.input.brand.SearchBrandsUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.BrandRepository;
import com.essenza.draco.shared.common.domain.dto.PageResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchBrandsService implements SearchBrandsUseCase {

    private final BrandRepository repository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BrandDto> search(String query, Pageable pageable) {
        return PageResponse.from(repository.search(query, pageable));
    }
}

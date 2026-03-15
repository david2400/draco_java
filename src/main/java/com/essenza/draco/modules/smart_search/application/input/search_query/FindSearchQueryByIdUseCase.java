package com.essenza.draco.modules.smart_search.application.input.search_query;

import com.essenza.draco.modules.smart_search.domain.dto.search_query.SearchQueryDto;

import java.util.Optional;

public interface FindSearchQueryByIdUseCase {
    Optional<SearchQueryDto> findById(Long id);
}

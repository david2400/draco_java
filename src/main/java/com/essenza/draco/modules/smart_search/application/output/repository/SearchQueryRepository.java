package com.essenza.draco.modules.smart_search.application.output.repository;

import com.essenza.draco.modules.smart_search.domain.dto.search_query.CreateSearchQueryDto;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.SearchQueryDto;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.UpdateSearchQueryDto;

import java.util.List;
import java.util.Optional;

public interface SearchQueryRepository {

    SearchQueryDto create(CreateSearchQueryDto input);

    SearchQueryDto update(Long id, UpdateSearchQueryDto input);

    boolean deleteById(Long id);

    Optional<SearchQueryDto> findById(Long id);

    List<SearchQueryDto> findAll();
}

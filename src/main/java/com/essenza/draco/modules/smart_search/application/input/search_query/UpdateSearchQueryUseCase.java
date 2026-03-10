package com.essenza.draco.modules.smart_search.application.input.search_query;

import com.essenza.draco.modules.smart_search.domain.dto.search_query.SearchQueryDto;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.UpdateSearchQueryDto;

public interface UpdateSearchQueryUseCase {
    SearchQueryDto update(Long id, UpdateSearchQueryDto input);
}

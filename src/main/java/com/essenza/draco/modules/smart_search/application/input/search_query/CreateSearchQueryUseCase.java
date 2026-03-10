package com.essenza.draco.modules.smart_search.application.input.search_query;

import com.essenza.draco.modules.smart_search.domain.dto.search_query.CreateSearchQueryDto;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.SearchQueryDto;

public interface CreateSearchQueryUseCase {
    SearchQueryDto create(CreateSearchQueryDto input);
}

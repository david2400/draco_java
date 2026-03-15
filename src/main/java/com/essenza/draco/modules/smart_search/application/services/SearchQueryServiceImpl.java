package com.essenza.draco.modules.smart_search.application.services;

import com.essenza.draco.modules.smart_search.application.input.search_query.CreateSearchQueryUseCase;
import com.essenza.draco.modules.smart_search.application.input.search_query.DeleteSearchQueryUseCase;
import com.essenza.draco.modules.smart_search.application.input.search_query.FindSearchQueriesUseCase;
import com.essenza.draco.modules.smart_search.application.input.search_query.FindSearchQueryByIdUseCase;
import com.essenza.draco.modules.smart_search.application.input.search_query.UpdateSearchQueryUseCase;
import com.essenza.draco.modules.smart_search.application.output.repository.SearchQueryRepository;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.CreateSearchQueryDto;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.SearchQueryDto;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.UpdateSearchQueryDto;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SearchQueryServiceImpl implements CreateSearchQueryUseCase,
        UpdateSearchQueryUseCase,
        DeleteSearchQueryUseCase,
        FindSearchQueryByIdUseCase,
        FindSearchQueriesUseCase {

    private final SearchQueryRepository repository;

    public SearchQueryServiceImpl(SearchQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchQueryDto create(CreateSearchQueryDto input) {
        if (input.getLastRunAt() == null) {
            input.setLastRunAt(Instant.now());
        }
        return repository.create(input);
    }

    @Override
    public SearchQueryDto update(Long id, UpdateSearchQueryDto input) {
        repository.findById(id).orElseThrow(() -> new NotFoundException("Search query not found: " + id));
        if (input.getLastRunAt() == null) {
            input.setLastRunAt(Instant.now());
        }
        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SearchQueryDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQueryDto> findAll() {
        return repository.findAll();
    }
}

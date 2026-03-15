package com.essenza.draco.modules.smart_search.infrastructure.outbound.repositories.search_query;

import com.essenza.draco.modules.smart_search.application.output.repository.SearchQueryRepository;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.CreateSearchQueryDto;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.SearchQueryDto;
import com.essenza.draco.modules.smart_search.domain.dto.search_query.UpdateSearchQueryDto;
import com.essenza.draco.modules.smart_search.infrastructure.outbound.mappers.SearchQueryMapper;
import com.essenza.draco.modules.smart_search.infrastructure.outbound.persistence.mysql.JpaSearchQueryRepository;
import com.essenza.draco.modules.smart_search.infrastructure.outbound.persistence.mysql.SearchQueryEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SearchQueryRepositoryAdapter implements SearchQueryRepository {

    private final JpaSearchQueryRepository jpa;
    private final SearchQueryMapper mapper;

    public SearchQueryRepositoryAdapter(JpaSearchQueryRepository jpa,
                                        SearchQueryMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public SearchQueryDto create(CreateSearchQueryDto input) {
        SearchQueryEntity entity = mapper.toEntity(input);
        SearchQueryEntity saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public SearchQueryDto update(Long id, UpdateSearchQueryDto input) {
        SearchQueryEntity entity = jpa.findById(id)
                .orElseThrow(() -> new NotFoundException("Search query not found: " + id));
        mapper.updateEntityFromDto(input, entity);
        SearchQueryEntity updated = jpa.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) {
            return false;
        }
        jpa.deleteById(id);
        return true;
    }

    @Override
    public Optional<SearchQueryDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<SearchQueryDto> findAll() {
        return mapper.toDtoList(jpa.findAll());
    }
}

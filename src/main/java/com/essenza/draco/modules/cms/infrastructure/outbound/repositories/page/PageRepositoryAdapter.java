package com.essenza.draco.modules.cms.infrastructure.outbound.repositories.page;

import com.essenza.draco.modules.cms.application.output.repository.PageRepository;
import com.essenza.draco.modules.cms.domain.dto.page.CreatePageDto;
import com.essenza.draco.modules.cms.domain.dto.page.PageDto;
import com.essenza.draco.modules.cms.domain.dto.page.UpdatePageDto;
import com.essenza.draco.modules.cms.infrastructure.outbound.mappers.PageMapper;
import com.essenza.draco.modules.cms.infrastructure.outbound.persistence.mysql.PageEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PageRepositoryAdapter implements PageRepository {

    private final JpaPageRepository jpa;
    private final PageMapper mapper;

    public PageRepositoryAdapter(JpaPageRepository jpa, PageMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public PageDto create(CreatePageDto input) {
        PageEntity entity = mapper.toEntity(input);
        PageEntity saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public PageDto update(Long id, UpdatePageDto input) {
        PageEntity entity = jpa.findById(id)
                .orElseThrow(() -> new NotFoundException("Page not found: " + id));
        mapper.updateEntityFromDto(input, entity);
        PageEntity updated = jpa.save(entity);
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
    public Optional<PageDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<PageDto> findAll() {
        return mapper.toDtoList(jpa.findAll());
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpa.existsBySlug(slug);
    }

    @Override
    public boolean existsBySlugExcludingId(String slug, Long id) {
        return jpa.existsBySlugAndIdNot(slug, id);
    }
}

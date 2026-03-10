package com.essenza.draco.modules.cms.application.output.repository;

import com.essenza.draco.modules.cms.domain.dto.page.CreatePageDto;
import com.essenza.draco.modules.cms.domain.dto.page.PageDto;
import com.essenza.draco.modules.cms.domain.dto.page.UpdatePageDto;

import java.util.List;
import java.util.Optional;

public interface PageRepository {

    PageDto create(CreatePageDto input);

    PageDto update(Long id, UpdatePageDto input);

    boolean deleteById(Long id);

    Optional<PageDto> findById(Long id);

    List<PageDto> findAll();

    boolean existsBySlug(String slug);

    boolean existsBySlugExcludingId(String slug, Long id);
}

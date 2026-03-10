package com.essenza.draco.modules.cms.application.input.page;

import com.essenza.draco.modules.cms.domain.dto.page.PageDto;

import java.util.List;

public interface FindPagesUseCase {
    List<PageDto> findAll();
}

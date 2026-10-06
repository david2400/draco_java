package com.essenza.draco.modules.cms.application.input.page;

import com.essenza.draco.modules.cms.application.dto.page.PageDto;

import java.util.Optional;

public interface FindPageByIdUseCase {
    Optional<PageDto> findById(Long id);
}

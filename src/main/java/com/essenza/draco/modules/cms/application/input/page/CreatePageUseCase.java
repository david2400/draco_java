package com.essenza.draco.modules.cms.application.input.page;

import com.essenza.draco.modules.cms.domain.dto.page.CreatePageDto;
import com.essenza.draco.modules.cms.domain.dto.page.PageDto;

public interface CreatePageUseCase {
    PageDto create(CreatePageDto input);
}

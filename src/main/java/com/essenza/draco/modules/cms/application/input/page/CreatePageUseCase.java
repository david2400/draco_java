package com.essenza.draco.modules.cms.application.input.page;

import com.essenza.draco.modules.cms.application.dto.page.CreatePageDto;
import com.essenza.draco.modules.cms.application.dto.page.PageDto;

public interface CreatePageUseCase {
    PageDto create(CreatePageDto input);
}

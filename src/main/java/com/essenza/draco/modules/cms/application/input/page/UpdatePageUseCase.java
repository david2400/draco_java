package com.essenza.draco.modules.cms.application.input.page;

import com.essenza.draco.modules.cms.application.dto.page.PageDto;
import com.essenza.draco.modules.cms.application.dto.page.UpdatePageDto;

public interface UpdatePageUseCase {
    PageDto update(Long id, UpdatePageDto input);
}

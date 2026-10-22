package com.essenza.draco.modules.billing.application.input.resolution;

import com.essenza.draco.modules.billing.application.dto.resolution.ResolutionDto;
import com.essenza.draco.modules.billing.application.dto.resolution.SaveResolutionDto;

public interface UpdateResolutionUseCase {
    ResolutionDto update(Long id, SaveResolutionDto input);
}

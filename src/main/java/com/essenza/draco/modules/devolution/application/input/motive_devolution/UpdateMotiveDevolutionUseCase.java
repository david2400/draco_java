package com.essenza.draco.modules.devolution.application.input.motive_devolution;

import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.MotiveDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.UpdateMotiveDevolutionDto;

public interface UpdateMotiveDevolutionUseCase {
    MotiveDevolutionDto update(Long id, UpdateMotiveDevolutionDto input);
}

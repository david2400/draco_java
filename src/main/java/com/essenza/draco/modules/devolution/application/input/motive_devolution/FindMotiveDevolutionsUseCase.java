package com.essenza.draco.modules.devolution.application.input.motive_devolution;

import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.MotiveDevolutionDto;

import java.util.List;

public interface FindMotiveDevolutionsUseCase {
    List<MotiveDevolutionDto> findAll();
}

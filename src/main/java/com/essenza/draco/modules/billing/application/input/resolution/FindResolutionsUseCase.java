package com.essenza.draco.modules.billing.application.input.resolution;

import java.util.List;

import com.essenza.draco.modules.billing.application.dto.resolution.ResolutionDto;

public interface FindResolutionsUseCase {
    List<ResolutionDto> findAll(String kind);

    ResolutionDto findById(Long id);
}

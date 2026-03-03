package com.essenza.draco.modules.product_details.application.input.feature;

import com.essenza.draco.modules.product_details.domain.dto.feature.FeatureDto;
import com.essenza.draco.modules.product_details.domain.dto.feature.UpdateFeatureDto;

public interface UpdateFeatureUseCase {
    FeatureDto update(Long id, UpdateFeatureDto input);
}

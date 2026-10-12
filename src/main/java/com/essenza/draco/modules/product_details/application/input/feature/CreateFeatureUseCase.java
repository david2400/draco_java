package com.essenza.draco.modules.product_details.application.input.feature;

import com.essenza.draco.modules.product_details.domain.dto.feature.CreateFeatureDto;
import com.essenza.draco.modules.product_details.domain.dto.feature.FeatureDto;

public interface CreateFeatureUseCase {
    FeatureDto create(CreateFeatureDto input);
}

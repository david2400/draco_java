package com.essenza.draco.modules.product_details.application.input.type_product_feature;

import com.essenza.draco.modules.product_details.domain.dto.type_product_feature.TypeProductFeatureDto;

import java.util.List;

public interface FindTypeProductFeaturesUseCase {
    List<TypeProductFeatureDto> findAll();
}

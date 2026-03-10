package com.essenza.draco.modules.recommendations.application.input.product_recommendation;

import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.ProductRecommendationDto;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.UpdateProductRecommendationDto;

public interface UpdateProductRecommendationUseCase {
    ProductRecommendationDto update(Long id, UpdateProductRecommendationDto input);
}

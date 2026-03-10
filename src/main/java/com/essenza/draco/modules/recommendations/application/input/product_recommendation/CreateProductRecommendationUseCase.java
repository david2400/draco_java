package com.essenza.draco.modules.recommendations.application.input.product_recommendation;

import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.CreateProductRecommendationDto;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.ProductRecommendationDto;

public interface CreateProductRecommendationUseCase {
    ProductRecommendationDto create(CreateProductRecommendationDto input);
}

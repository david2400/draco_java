package com.essenza.draco.modules.recommendations.application.input.product_recommendation;

import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.ProductRecommendationDto;

import java.util.Optional;

public interface FindProductRecommendationByIdUseCase {
    Optional<ProductRecommendationDto> findById(Long id);
}

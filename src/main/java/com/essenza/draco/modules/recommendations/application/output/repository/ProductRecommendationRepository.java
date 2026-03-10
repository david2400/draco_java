package com.essenza.draco.modules.recommendations.application.output.repository;

import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.CreateProductRecommendationDto;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.ProductRecommendationDto;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.UpdateProductRecommendationDto;

import java.util.List;
import java.util.Optional;

public interface ProductRecommendationRepository {

    ProductRecommendationDto create(CreateProductRecommendationDto input);

    ProductRecommendationDto update(Long id, UpdateProductRecommendationDto input);

    boolean deleteById(Long id);

    Optional<ProductRecommendationDto> findById(Long id);

    List<ProductRecommendationDto> findAll();
}

package com.essenza.draco.modules.recommendations.application.services;

import com.essenza.draco.modules.recommendations.application.input.product_recommendation.CreateProductRecommendationUseCase;
import com.essenza.draco.modules.recommendations.application.input.product_recommendation.DeleteProductRecommendationUseCase;
import com.essenza.draco.modules.recommendations.application.input.product_recommendation.FindProductRecommendationByIdUseCase;
import com.essenza.draco.modules.recommendations.application.input.product_recommendation.FindProductRecommendationsUseCase;
import com.essenza.draco.modules.recommendations.application.input.product_recommendation.UpdateProductRecommendationUseCase;
import com.essenza.draco.modules.recommendations.application.output.repository.ProductRecommendationRepository;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.CreateProductRecommendationDto;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.ProductRecommendationDto;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.UpdateProductRecommendationDto;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductRecommendationServiceImpl implements CreateProductRecommendationUseCase,
        UpdateProductRecommendationUseCase,
        DeleteProductRecommendationUseCase,
        FindProductRecommendationByIdUseCase,
        FindProductRecommendationsUseCase {

    private final ProductRecommendationRepository repository;

    public ProductRecommendationServiceImpl(ProductRecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public ProductRecommendationDto create(CreateProductRecommendationDto input) {
        if (input.getScore() == null) {
            input.setScore(defaultScore(input));
        }
        if (input.getRecommendationType() == null) {
            input.setRecommendationType("CUSTOM");
        }
        input.setIsClicked(input.getIsClicked() != null && input.getIsClicked());
        input.setIsPurchased(input.getIsPurchased() != null && input.getIsPurchased());
        return repository.create(input);
    }

    @Override
    public ProductRecommendationDto update(Long id, UpdateProductRecommendationDto input) {
        repository.findById(id).orElseThrow(() -> new NotFoundException("Recommendation not found: " + id));
        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductRecommendationDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductRecommendationDto> findAll() {
        return repository.findAll();
    }

    private static java.math.BigDecimal defaultScore(CreateProductRecommendationDto input) {
        return java.math.BigDecimal.valueOf(0.5);
    }
}

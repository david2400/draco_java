package com.essenza.draco.modules.recommendations.infrastructure.outbound.repositories.product_recommendation;

import com.essenza.draco.modules.recommendations.application.output.repository.ProductRecommendationRepository;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.CreateProductRecommendationDto;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.ProductRecommendationDto;
import com.essenza.draco.modules.recommendations.domain.dto.product_recommendation.UpdateProductRecommendationDto;
import com.essenza.draco.modules.recommendations.infrastructure.outbound.mappers.ProductRecommendationMapper;
import com.essenza.draco.modules.recommendations.infrastructure.outbound.persistence.mysql.ProductRecommendationEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRecommendationRepositoryAdapter implements ProductRecommendationRepository {

    private final JpaProductRecommendationRepository jpa;
    private final ProductRecommendationMapper mapper;

    public ProductRecommendationRepositoryAdapter(JpaProductRecommendationRepository jpa,
                                                  ProductRecommendationMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public ProductRecommendationDto create(CreateProductRecommendationDto input) {
        ProductRecommendationEntity entity = mapper.toEntity(input);
        ProductRecommendationEntity saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public ProductRecommendationDto update(Long id, UpdateProductRecommendationDto input) {
        ProductRecommendationEntity entity = jpa.findById(id)
                .orElseThrow(() -> new NotFoundException("Recommendation not found: " + id));
        mapper.updateEntityFromDto(input, entity);
        ProductRecommendationEntity updated = jpa.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) {
            return false;
        }
        jpa.deleteById(id);
        return true;
    }

    @Override
    public Optional<ProductRecommendationDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<ProductRecommendationDto> findAll() {
        return mapper.toDtoList(jpa.findAll());
    }
}

package com.essenza.draco.modules.recommendations.infrastructure.outbound.repositories.product_recommendation;

import com.essenza.draco.modules.recommendations.infrastructure.outbound.persistence.mysql.ProductRecommendationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductRecommendationRepository extends JpaRepository<ProductRecommendationEntity, Long> {
}

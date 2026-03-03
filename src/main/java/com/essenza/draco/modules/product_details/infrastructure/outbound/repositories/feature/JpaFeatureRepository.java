package com.essenza.draco.modules.product_details.infrastructure.outbound.repositories.feature;

import com.essenza.draco.modules.product_details.infrastructure.outbound.persistence.mysql.shop.FeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaFeatureRepository extends JpaRepository<FeatureEntity, Long> {
}

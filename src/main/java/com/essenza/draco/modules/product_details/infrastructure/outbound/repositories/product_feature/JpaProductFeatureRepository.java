package com.essenza.draco.modules.product_details.infrastructure.outbound.repositories.product_feature;

import com.essenza.draco.modules.product_details.infrastructure.outbound.persistence.mysql.shop.ProductFeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductFeatureRepository extends JpaRepository<ProductFeatureEntity, ProductFeatureEntity.PK> {
}

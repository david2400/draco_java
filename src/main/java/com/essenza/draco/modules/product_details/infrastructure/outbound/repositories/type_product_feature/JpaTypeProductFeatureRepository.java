package com.essenza.draco.modules.product_details.infrastructure.outbound.repositories.type_product_feature;

import com.essenza.draco.modules.product_details.infrastructure.outbound.persistence.mysql.shop.TypeProductFeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTypeProductFeatureRepository extends JpaRepository<TypeProductFeatureEntity, Long> {
    boolean existsByTypeProductId(Long typeProductId);
    boolean existsByFeatureId(Long featureId);
    void deleteByTypeProductId(Long typeProductId);
}

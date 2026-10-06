package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku;

import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductImageEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    List<ProductImageEntity> findByProductId(Long productId);

    List<ProductImageEntity> findByProductIdIn(Collection<Long> productIds);
}

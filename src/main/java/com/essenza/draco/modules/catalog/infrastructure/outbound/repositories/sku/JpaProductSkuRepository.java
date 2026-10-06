package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku;

import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductSkuEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaProductSkuRepository extends JpaRepository<ProductSkuEntity, Long> {

    List<ProductSkuEntity> findByProductId(Long productId);

    List<ProductSkuEntity> findByProductIdIn(Collection<Long> productIds);

    /** Incluye filas con borrado lógico: el índice único de {@code code} también las cuenta. */
    @Query(value = "select count(*) from product_skus where code = :code", nativeQuery = true)
    long countByCodeIncludingDeleted(@Param("code") String code);
}

package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.cash;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.StoreEntity;

public interface JpaStoreRepository extends JpaRepository<StoreEntity, Long> {

    @Query("select count(s) > 0 from StoreEntity s where s.code = :code and (:excludeId is null or s.id <> :excludeId)")
    boolean existsCode(@Param("code") String code, @Param("excludeId") Long excludeId);
}

package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.cash;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.CashRegisterEntity;

public interface JpaCashRegisterRepository extends JpaRepository<CashRegisterEntity, Long> {

    @Query("select count(r) > 0 from CashRegisterEntity r where r.code = :code and (:excludeId is null or r.id <> :excludeId)")
    boolean existsCode(@Param("code") String code, @Param("excludeId") Long excludeId);
}

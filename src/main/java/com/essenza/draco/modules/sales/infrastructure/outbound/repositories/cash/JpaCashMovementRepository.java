package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.cash;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.CashMovementEntity;

public interface JpaCashMovementRepository extends JpaRepository<CashMovementEntity, Long> {

    List<CashMovementEntity> findByCashSessionIdOrderByIdAsc(Long cashSessionId);
}

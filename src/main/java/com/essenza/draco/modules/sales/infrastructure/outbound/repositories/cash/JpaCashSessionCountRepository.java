package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.cash;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.CashSessionCountEntity;

public interface JpaCashSessionCountRepository extends JpaRepository<CashSessionCountEntity, Long> {

    List<CashSessionCountEntity> findByCashSessionIdOrderByIdAsc(Long cashSessionId);
}

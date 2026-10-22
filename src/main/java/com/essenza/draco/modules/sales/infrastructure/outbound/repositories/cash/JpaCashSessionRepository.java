package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.cash;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.CashSessionEntity;

import jakarta.persistence.LockModeType;

public interface JpaCashSessionRepository extends JpaRepository<CashSessionEntity, Long>, JpaSpecificationExecutor<CashSessionEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from CashSessionEntity s where s.id = :id")
    Optional<CashSessionEntity> lockById(@Param("id") Long id);

    List<CashSessionEntity> findByCashRegisterIdAndStatus(Long cashRegisterId, String status);

    long countByCashRegisterId(Long cashRegisterId);
}

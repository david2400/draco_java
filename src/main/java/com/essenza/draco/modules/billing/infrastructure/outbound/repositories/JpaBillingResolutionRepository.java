package com.essenza.draco.modules.billing.infrastructure.outbound.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.essenza.draco.modules.billing.infrastructure.outbound.persistence.BillingResolutionEntity;

import jakarta.persistence.LockModeType;

public interface JpaBillingResolutionRepository extends JpaRepository<BillingResolutionEntity, Long> {

    List<BillingResolutionEntity> findAllByOrderByKindAscIdDesc();

    List<BillingResolutionEntity> findByKindOrderByIdDesc(String kind);

    /** Activas con números disponibles, la más antigua primero (se agota antes de usar la siguiente). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select r from BillingResolutionEntity r
             where r.kind = :kind and r.environment = :environment and r.active = true and r.nextNumber <= r.toNumber
             order by r.id asc""")
    List<BillingResolutionEntity> lockActive(@Param("kind") String kind, @Param("environment") String environment);

    @Query(value = "SELECT COUNT(*) FROM invoices WHERE resolution_id = :id", nativeQuery = true)
    long documentsUsing(@Param("id") Long id);
}

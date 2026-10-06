package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.order;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    List<OrderEntity> findByCreatedAtBetween(Instant startDate, Instant endDate);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select o from OrderEntity o where o.id = :id")
    java.util.Optional<OrderEntity> findForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}

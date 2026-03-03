package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.order;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByCreatedAtBetween(Instant startDate, Instant endDate);
}

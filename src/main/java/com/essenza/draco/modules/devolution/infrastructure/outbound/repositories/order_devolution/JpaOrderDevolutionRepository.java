package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.order_devolution;

import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.OrderDevolutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderDevolutionRepository extends JpaRepository<OrderDevolutionEntity, Long> {
}

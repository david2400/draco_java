package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.order_devolution_detail;

import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.OrderDevolutionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderDevolutionDetailRepository extends JpaRepository<OrderDevolutionDetailEntity, Long> {
}

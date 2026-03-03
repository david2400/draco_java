package com.essenza.draco.modules.devolution.infrastructure.outbound.repositories.refund_method;

import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.RefundMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaRefundMethodRepository extends JpaRepository<RefundMethodEntity, Long> {
    Optional<RefundMethodEntity> findByName(String name);
}

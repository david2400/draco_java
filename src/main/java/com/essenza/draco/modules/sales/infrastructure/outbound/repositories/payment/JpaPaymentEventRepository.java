package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.PaymentEventEntity;

public interface JpaPaymentEventRepository extends JpaRepository<PaymentEventEntity, Long> {

    boolean existsBySourceAndEventKey(String source, String eventKey);

    List<PaymentEventEntity> findByPaymentIdOrderByIdAsc(Long paymentId);
}

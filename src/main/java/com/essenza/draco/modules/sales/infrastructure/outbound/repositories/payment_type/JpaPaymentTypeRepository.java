package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.payment_type;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.PaymentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPaymentTypeRepository extends JpaRepository<PaymentTypeEntity, Long> {
}

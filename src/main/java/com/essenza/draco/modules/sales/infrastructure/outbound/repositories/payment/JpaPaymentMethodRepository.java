package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.PaymentMethodEntity;

public interface JpaPaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {

    @Query("select count(m) > 0 from PaymentMethodEntity m where upper(m.code) = upper(:code) and (:excludeId is null or m.id <> :excludeId)")
    boolean existsCode(@Param("code") String code, @Param("excludeId") Long excludeId);
}

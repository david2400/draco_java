package com.essenza.draco.modules.sales.application.output.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;

/** Catálogo de medios de pago (tabla payment_types). */
public interface PaymentMethodRepository {

    List<PaymentMethod> findAll();

    Optional<PaymentMethod> findById(Long id);

    PaymentMethod save(PaymentMethod method);

    boolean existsActiveCode(String code, Long excludeId);

    void deleteById(Long id);

    /** Pagos por medio (id → cantidad). */
    Map<Long, Long> paymentCounts();
}

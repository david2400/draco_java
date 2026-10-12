package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.payment;

import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.output.repository.PaymentEventLog;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.PaymentEventEntity;

@Repository
@RequiredArgsConstructor
public class PaymentEventLogAdapter implements PaymentEventLog {

    private final JpaPaymentEventRepository jpa;

    @Override
    public boolean record(Event event) {
        if (event.eventKey() != null && jpa.existsBySourceAndEventKey(event.source(), event.eventKey())) {
            return false;
        }
        jpa.save(PaymentEventEntity.builder()
                .paymentId(event.paymentId())
                .source(event.source())
                .eventKey(event.eventKey())
                .type(event.type())
                .fromStatus(event.fromStatus())
                .toStatus(event.toStatus())
                .amount(event.amount())
                .detail(event.detail())
                .payload(event.payload())
                .createdAt(event.createdAt())
                .build());
        return true;
    }

    @Override
    public boolean exists(String source, String eventKey) {
        return jpa.existsBySourceAndEventKey(source, eventKey);
    }

    @Override
    public List<Event> forPayment(Long paymentId) {
        return jpa.findByPaymentIdOrderByIdAsc(paymentId).stream()
                .map(e -> new Event(e.getId(), e.getPaymentId(), e.getSource(), e.getEventKey(), e.getType(),
                        e.getFromStatus(), e.getToStatus(), e.getAmount(), e.getDetail(), null, e.getCreatedAt()))
                .toList();
    }
}

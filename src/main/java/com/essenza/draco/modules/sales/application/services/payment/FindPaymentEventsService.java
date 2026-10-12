package com.essenza.draco.modules.sales.application.services.payment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentEventDto;
import com.essenza.draco.modules.sales.application.input.payment.FindPaymentEventsUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentEventLog;
import com.essenza.draco.modules.sales.application.output.repository.PaymentRepository;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindPaymentEventsService implements FindPaymentEventsUseCase {

    private final PaymentRepository payments;
    private final PaymentEventLog events;

    @Override
    public List<PaymentEventDto> events(Long paymentId) {
        payments.findById(paymentId).orElseThrow(() -> new NotFoundException("Pago no encontrado: " + paymentId));
        return events.forPayment(paymentId).stream()
                .map(event -> new PaymentEventDto(event.id(), event.source(), event.type(), event.fromStatus(),
                        event.toStatus(), event.amount(), event.detail(), event.createdAt()))
                .toList();
    }
}

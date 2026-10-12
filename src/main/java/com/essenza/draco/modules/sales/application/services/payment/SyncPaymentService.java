package com.essenza.draco.modules.sales.application.services.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;
import com.essenza.draco.modules.sales.application.input.payment.SyncPaymentUseCase;
import com.essenza.draco.modules.sales.application.output.gateway.PaymentGateway;
import com.essenza.draco.modules.sales.domain.model.payment.Payment;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentRuleViolation;

/** Consulta en la pasarela el estado de un pago en línea (por si no llegó el webhook). */
@Service
@Transactional
@RequiredArgsConstructor
public class SyncPaymentService implements SyncPaymentUseCase {

    private final PaymentSoporte soporte;

    @Override
    public PaymentDto sync(Long id) {
        Payment payment = soporte.locked(id);
        if (payment.getProvider() == null) {
            throw new PaymentRuleViolation("Solo los pagos en línea se consultan en la pasarela.");
        }
        PaymentGateway gateway = soporte.gateway(payment.getProvider());
        gateway.fetchStatus(payment.getPaymentReference(), payment.getProviderReference())
                .ifPresent(status -> soporte.applyGatewayStatus(payment, status.status(), status.providerReference(),
                        status.amount(), status.detail(), payment.getProvider().name(), null, null));
        return soporte.toDto(soporte.locked(id));
    }
}

package com.essenza.draco.modules.sales.application.services.payment;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentActionDto;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;
import com.essenza.draco.modules.sales.application.input.payment.ConfirmPaymentUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;
import com.essenza.draco.modules.sales.application.output.repository.PaymentRepository;
import com.essenza.draco.modules.sales.domain.model.payment.Payment;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentRuleViolation;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentStatus;

/** Aprueba a mano un pago pendiente (transferencia vista en el banco, contraentrega cobrada…). */
@Service
@Transactional
@RequiredArgsConstructor
public class ConfirmPaymentService implements ConfirmPaymentUseCase {

    private final PaymentRepository payments;
    private final PaymentMethodRepository methods;
    private final PaymentSoporte soporte;

    @Override
    public PaymentDto confirm(Long id, PaymentActionDto input) {
        Payment payment = soporte.locked(id);
        PaymentStatus from = payment.getStatus();
        if (input != null) {
            payment.recordReference(input.reference());
        }
        boolean needsReference = methods.findById(payment.getMethodId()).map(method -> method.requiresReference()).orElse(false);
        if (needsReference && payment.getReference() == null) {
            throw new PaymentRuleViolation("Este medio exige la referencia del pago para confirmarlo.");
        }
        payment.approve(Instant.now(), null, input == null ? null : PaymentSoporte.blankToNull(input.reason()));
        Payment saved = payments.save(payment);
        soporte.log(saved, PaymentSoporte.SOURCE_PANEL, null, "CONFIRMED", from, saved.getAmount(), saved.getStatusDetail(), null);
        soporte.settleOrder(saved.getOrderId());
        return soporte.toDto(saved);
    }
}

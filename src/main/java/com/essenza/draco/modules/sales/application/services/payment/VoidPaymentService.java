package com.essenza.draco.modules.sales.application.services.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentActionDto;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;
import com.essenza.draco.modules.sales.application.input.payment.VoidPaymentUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentRepository;
import com.essenza.draco.modules.sales.domain.model.payment.Payment;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentRuleViolation;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentStatus;

/**
 * Anula un pago (registrado por error, voucher anulado en el datáfono…). No cambia el estado de
 * la orden: el saldo vuelve a quedar pendiente y se ve en el cobro de la orden.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class VoidPaymentService implements VoidPaymentUseCase {

    private final PaymentRepository payments;
    private final PaymentSoporte soporte;

    @Override
    public PaymentDto voidPayment(Long id, PaymentActionDto input) {
        String reason = input == null ? null : PaymentSoporte.blankToNull(input.reason());
        if (reason == null) {
            throw new PaymentRuleViolation("Indica el motivo de la anulación.");
        }
        Payment payment = soporte.locked(id);
        PaymentStatus from = payment.getStatus();
        payment.voidPayment(reason);
        Payment saved = payments.save(payment);
        soporte.log(saved, PaymentSoporte.SOURCE_PANEL, null, "VOIDED", from, saved.getAmount(), reason, null);
        return soporte.toDto(saved);
    }
}

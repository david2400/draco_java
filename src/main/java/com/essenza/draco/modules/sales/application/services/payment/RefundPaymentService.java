package com.essenza.draco.modules.sales.application.services.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentActionDto;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;
import com.essenza.draco.modules.sales.application.input.payment.RefundPaymentUseCase;
import com.essenza.draco.modules.sales.application.output.gateway.PaymentGateway;
import com.essenza.draco.modules.sales.application.output.repository.PaymentRepository;
import com.essenza.draco.modules.sales.domain.model.payment.Payment;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentRuleViolation;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentStatus;

/**
 * Reembolso parcial o total. Si la pasarela permite reembolsar por API (Mercado Pago) se pide
 * allá primero; en las demás (Wompi, pagos físicos) se registra el reembolso hecho por fuera.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RefundPaymentService implements RefundPaymentUseCase {

    private final PaymentRepository payments;
    private final PaymentSoporte soporte;

    @Override
    public PaymentDto refund(Long id, PaymentActionDto input) {
        Payment payment = soporte.locked(id);
        PaymentStatus from = payment.getStatus();
        BigDecimal amount = input == null || input.amount() == null ? payment.refundable()
                : input.amount().setScale(2, RoundingMode.HALF_UP);
        String reason = input == null ? null : PaymentSoporte.blankToNull(input.reason());
        if (amount.signum() <= 0) {
            throw new PaymentRuleViolation("El pago no tiene saldo para reembolsar.");
        }
        String detail = reason;
        if (payment.getProvider() != null) {
            PaymentGateway gateway = soporte.gateway(payment.getProvider());
            if (gateway.supportsRefund() && payment.getProviderReference() != null) {
                gateway.refund(payment.getProviderReference(), amount,
                        payment.getPaymentReference() + "-R" + payment.getRefundedAmount().add(amount).toPlainString());
                detail = (reason == null ? "" : reason + " · ") + "Reembolso solicitado a " + payment.getProvider();
            } else {
                detail = (reason == null ? "" : reason + " · ") + "Reembolso registrado: hazlo también en " + payment.getProvider();
            }
        }
        payment.refund(amount, detail);
        Payment saved = payments.save(payment);
        soporte.log(saved, PaymentSoporte.SOURCE_PANEL, null, "REFUNDED", from, amount, detail, null);
        return soporte.toDto(saved);
    }
}

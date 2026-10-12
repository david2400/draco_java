package com.essenza.draco.modules.sales.application.services.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentActionDto;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;
import com.essenza.draco.modules.sales.application.input.payment.DeclinePaymentUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentRepository;
import com.essenza.draco.modules.sales.domain.model.payment.Payment;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentStatus;

@Service
@Transactional
@RequiredArgsConstructor
public class DeclinePaymentService implements DeclinePaymentUseCase {

    private final PaymentRepository payments;
    private final PaymentSoporte soporte;

    @Override
    public PaymentDto decline(Long id, PaymentActionDto input) {
        Payment payment = soporte.locked(id);
        PaymentStatus from = payment.getStatus();
        payment.decline(null, input == null ? null : PaymentSoporte.blankToNull(input.reason()));
        Payment saved = payments.save(payment);
        soporte.log(saved, PaymentSoporte.SOURCE_PANEL, null, "DECLINED", from, saved.getAmount(), saved.getStatusDetail(), null);
        return soporte.toDto(saved);
    }
}

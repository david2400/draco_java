package com.essenza.draco.modules.sales.application.services.payment;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;
import com.essenza.draco.modules.sales.application.input.payment.FindPaymentByIdUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindPaymentByIdService implements FindPaymentByIdUseCase {

    private final PaymentRepository payments;
    private final PaymentSoporte soporte;

    @Override
    public Optional<PaymentDto> findById(Long id) {
        return payments.findById(id).map(soporte::toDto);
    }
}

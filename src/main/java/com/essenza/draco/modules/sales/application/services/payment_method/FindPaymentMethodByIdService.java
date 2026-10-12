package com.essenza.draco.modules.sales.application.services.payment_method;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment_method.PaymentMethodDto;
import com.essenza.draco.modules.sales.application.input.payment_method.FindPaymentMethodByIdUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindPaymentMethodByIdService implements FindPaymentMethodByIdUseCase {

    private final PaymentMethodRepository methods;
    private final PaymentMethodSoporte soporte;

    @Override
    public Optional<PaymentMethodDto> findById(Long id) {
        return methods.findById(id).map(method -> soporte.toDto(method, methods.paymentCounts()));
    }
}

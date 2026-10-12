package com.essenza.draco.modules.sales.application.services.payment_method;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment_method.PaymentMethodDto;
import com.essenza.draco.modules.sales.application.dto.payment_method.SavePaymentMethodDto;
import com.essenza.draco.modules.sales.application.input.payment_method.CreatePaymentMethodUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;

@Service
@Transactional
@RequiredArgsConstructor
public class CreatePaymentMethodService implements CreatePaymentMethodUseCase {

    private final PaymentMethodRepository methods;
    private final PaymentMethodSoporte soporte;

    @Override
    public PaymentMethodDto create(SavePaymentMethodDto input) {
        PaymentMethod method = soporte.toDomain(null, input);
        soporte.checkUniqueCode(method);
        return soporte.toDto(methods.save(method), Map.of());
    }
}

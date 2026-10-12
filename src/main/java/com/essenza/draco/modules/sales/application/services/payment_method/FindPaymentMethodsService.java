package com.essenza.draco.modules.sales.application.services.payment_method;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment_method.PaymentMethodDto;
import com.essenza.draco.modules.sales.application.input.payment_method.FindPaymentMethodsUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentChannel;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindPaymentMethodsService implements FindPaymentMethodsUseCase {

    private final PaymentMethodRepository methods;
    private final PaymentMethodSoporte soporte;

    @Override
    public List<PaymentMethodDto> findAll(boolean includeInactive, String channel) {
        PaymentChannel only = channel == null || channel.isBlank() ? null : PaymentChannel.parse(channel);
        Map<Long, Long> counts = methods.paymentCounts();
        return methods.findAll().stream()
                .filter(method -> includeInactive || method.active())
                .filter(method -> only == null || method.channel() == only)
                .sorted(Comparator.comparingInt(PaymentMethod::position).thenComparing(PaymentMethod::name))
                .map(method -> soporte.toDto(method, counts))
                .toList();
    }
}

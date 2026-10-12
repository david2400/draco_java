package com.essenza.draco.modules.sales.application.services.payment_method;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.input.payment_method.DeletePaymentMethodUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class DeletePaymentMethodService implements DeletePaymentMethodUseCase {

    private final PaymentMethodRepository methods;

    @Override
    public void delete(Long id) {
        PaymentMethod current = methods.findById(id)
                .orElseThrow(() -> new NotFoundException("Medio de pago no encontrado: " + id));
        long used = methods.paymentCounts().getOrDefault(id, 0L);
        if (used > 0) {
            throw new ConflictException("\"" + current.name() + "\" tiene " + used
                    + " pago(s) registrados: desactívalo en lugar de borrarlo.");
        }
        methods.deleteById(id);
    }
}

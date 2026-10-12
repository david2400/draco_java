package com.essenza.draco.modules.sales.application.services.payment_method;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment_method.PaymentMethodDto;
import com.essenza.draco.modules.sales.application.dto.payment_method.SavePaymentMethodDto;
import com.essenza.draco.modules.sales.application.input.payment_method.UpdatePaymentMethodUseCase;
import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdatePaymentMethodService implements UpdatePaymentMethodUseCase {

    private final PaymentMethodRepository methods;
    private final PaymentMethodSoporte soporte;

    @Override
    public PaymentMethodDto update(Long id, SavePaymentMethodDto input) {
        PaymentMethod current = methods.findById(id)
                .orElseThrow(() -> new NotFoundException("Medio de pago no encontrado: " + id));
        PaymentMethod method = soporte.toDomain(id, input);
        soporte.checkUniqueCode(method);
        var counts = methods.paymentCounts();
        long used = counts.getOrDefault(id, 0L);
        // Con pagos registrados no cambia la naturaleza del medio: los reportes por canal y tipo quedarían mal.
        if (used > 0 && (current.kind() != method.kind() || current.channel() != method.channel()
                || current.provider() != method.provider())) {
            throw new ConflictException("\"" + current.name()
                    + "\" ya tiene " + used + " pago(s): no se puede cambiar su canal, tipo ni pasarela. Crea otro medio.");
        }
        return soporte.toDto(methods.save(method), counts);
    }
}

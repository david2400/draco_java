package com.essenza.draco.modules.billing.application.services.invoice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;
import com.essenza.draco.modules.billing.application.input.invoice.FindInvoiceByIdUseCase;

@Service
@Transactional(readOnly = true)
public class FindInvoiceByIdService implements FindInvoiceByIdUseCase {

    private final InvoiceSoporte soporte;

    public FindInvoiceByIdService(InvoiceSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    public InvoiceDto findById(Long id) {
        return soporte.toDto(soporte.get(id));
    }
}

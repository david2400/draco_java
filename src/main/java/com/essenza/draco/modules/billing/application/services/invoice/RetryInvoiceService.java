package com.essenza.draco.modules.billing.application.services.invoice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;
import com.essenza.draco.modules.billing.application.input.invoice.RetryInvoiceUseCase;
import com.essenza.draco.modules.billing.domain.model.Invoice;
import com.essenza.draco.shared.exceptions.ConflictException;

@Service
@Transactional
public class RetryInvoiceService implements RetryInvoiceUseCase {

    private final InvoiceSoporte soporte;

    public RetryInvoiceService(InvoiceSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    public InvoiceDto retry(Long id) {
        Invoice document = soporte.locked(id);
        if (!document.canRetry()) {
            throw new ConflictException("El documento " + document.fullNumber() + " está " + document.status()
                    + ": solo se reenvían documentos pendientes o rechazados.");
        }
        return soporte.toDto(soporte.send(document, soporte.readyIssuer()));
    }
}

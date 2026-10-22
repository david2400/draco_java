package com.essenza.draco.modules.billing.application.services.invoice;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;
import com.essenza.draco.modules.billing.application.input.invoice.FindOrderInvoicesUseCase;
import com.essenza.draco.modules.billing.application.output.repository.InvoiceRepository;

@Service
@Transactional(readOnly = true)
public class FindOrderInvoicesService implements FindOrderInvoicesUseCase {

    private final InvoiceSoporte soporte;
    private final InvoiceRepository invoices;

    public FindOrderInvoicesService(InvoiceSoporte soporte, InvoiceRepository invoices) {
        this.soporte = soporte;
        this.invoices = invoices;
    }

    @Override
    public List<InvoiceDto> forOrder(Long orderId) {
        return invoices.findByOrderId(orderId).stream().map(soporte::toDto).toList();
    }
}

package com.essenza.draco.modules.billing.application.services.invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;
import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceFilter;
import com.essenza.draco.modules.billing.application.input.invoice.SearchInvoicesUseCase;
import com.essenza.draco.modules.billing.application.output.repository.InvoiceRepository;

@Service
@Transactional(readOnly = true)
public class SearchInvoicesService implements SearchInvoicesUseCase {

    private final InvoiceRepository invoices;

    public SearchInvoicesService(InvoiceRepository invoices) {
        this.invoices = invoices;
    }

    @Override
    public Page<InvoiceDto> search(InvoiceFilter filter, Pageable pageable) {
        return invoices.search(filter, pageable).map(InvoiceSoporte::summaryDto);
    }
}

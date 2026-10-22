package com.essenza.draco.modules.billing.application.services.invoice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceFilter;
import com.essenza.draco.modules.billing.application.dto.invoice.InvoicesSummaryDto;
import com.essenza.draco.modules.billing.application.input.invoice.SummarizeInvoicesUseCase;
import com.essenza.draco.modules.billing.application.output.repository.InvoiceRepository;

@Service
@Transactional(readOnly = true)
public class SummarizeInvoicesService implements SummarizeInvoicesUseCase {

    private final InvoiceRepository invoices;

    public SummarizeInvoicesService(InvoiceRepository invoices) {
        this.invoices = invoices;
    }

    @Override
    public InvoicesSummaryDto summary(InvoiceFilter filter) {
        InvoiceRepository.Totals t = invoices.totals(filter);
        return InvoicesSummaryDto.builder()
                .invoices(t.invoices())
                .creditNotes(t.creditNotes())
                .pending(t.pending())
                .invoicedTotal(t.invoicedTotal())
                .creditedTotal(t.creditedTotal())
                .netTotal(t.invoicedTotal().subtract(t.creditedTotal()))
                .taxTotal(t.invoicedTax().subtract(t.creditedTax()))
                .creditedTax(t.creditedTax())
                .build();
    }
}

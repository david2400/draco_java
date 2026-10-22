package com.essenza.draco.modules.billing.application.input.invoice;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceFilter;
import com.essenza.draco.modules.billing.application.dto.invoice.InvoicesSummaryDto;

public interface SummarizeInvoicesUseCase {
    InvoicesSummaryDto summary(InvoiceFilter filter);
}

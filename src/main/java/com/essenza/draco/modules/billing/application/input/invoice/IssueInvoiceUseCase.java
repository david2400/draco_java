package com.essenza.draco.modules.billing.application.input.invoice;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;
import com.essenza.draco.modules.billing.application.dto.invoice.IssueInvoiceDto;

public interface IssueInvoiceUseCase {
    InvoiceDto issue(IssueInvoiceDto input);
}

package com.essenza.draco.modules.billing.application.input.invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;
import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceFilter;

public interface SearchInvoicesUseCase {
    Page<InvoiceDto> search(InvoiceFilter filter, Pageable pageable);
}

package com.essenza.draco.modules.billing.application.input.invoice;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;

public interface FindInvoiceByIdUseCase {
    InvoiceDto findById(Long id);
}

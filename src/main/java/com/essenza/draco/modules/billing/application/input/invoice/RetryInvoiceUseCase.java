package com.essenza.draco.modules.billing.application.input.invoice;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;

public interface RetryInvoiceUseCase {
    /** Reenvía al proveedor un documento pendiente o rechazado (conserva el número). */
    InvoiceDto retry(Long id);
}

package com.essenza.draco.modules.billing.application.input.invoice;

import com.essenza.draco.modules.billing.application.dto.invoice.CreditNoteDto;
import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;

public interface CreateCreditNoteUseCase {
    /** Nota crédito total que anula la factura. */
    InvoiceDto creditNote(Long invoiceId, CreditNoteDto input);
}

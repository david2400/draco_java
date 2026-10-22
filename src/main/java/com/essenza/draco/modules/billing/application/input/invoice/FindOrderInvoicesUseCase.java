package com.essenza.draco.modules.billing.application.input.invoice;

import java.util.List;

import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;

public interface FindOrderInvoicesUseCase {
    List<InvoiceDto> forOrder(Long orderId);
}

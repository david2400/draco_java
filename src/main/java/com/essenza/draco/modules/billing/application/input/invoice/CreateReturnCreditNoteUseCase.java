package com.essenza.draco.modules.billing.application.input.invoice;

import java.util.Optional;
import com.essenza.draco.modules.billing.application.dto.invoice.InvoiceDto;
import com.essenza.draco.shared.common.devolution.DevolutionRefundedEvent;

public interface CreateReturnCreditNoteUseCase {

    /**
     * Nota crédito parcial por las líneas devueltas si la orden tiene factura emitida; vacío si
     * no aplica (sin factura emitida). Idempotente por devolución.
     */
    Optional<InvoiceDto> forDevolution(DevolutionRefundedEvent event);
}

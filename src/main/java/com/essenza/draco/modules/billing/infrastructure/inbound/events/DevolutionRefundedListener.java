package com.essenza.draco.modules.billing.infrastructure.inbound.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import com.essenza.draco.modules.billing.application.input.invoice.CreateReturnCreditNoteUseCase;
import com.essenza.draco.shared.common.devolution.DevolutionRefundedEvent;

/** Tras reembolsar una devolución emite la nota crédito parcial; un fallo no revierte la devolución. */
@Component
public class DevolutionRefundedListener {

    private static final Logger log = LoggerFactory.getLogger(DevolutionRefundedListener.class);

    private final CreateReturnCreditNoteUseCase creditNote;

    public DevolutionRefundedListener(CreateReturnCreditNoteUseCase creditNote) {
        this.creditNote = creditNote;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRefunded(DevolutionRefundedEvent event) {
        try {
            creditNote.forDevolution(event);
        } catch (RuntimeException ex) {
            log.warn("No se pudo emitir la nota crédito de la devolución {} (orden {}): {}", event.devolutionId(),
                    event.orderId(), ex.getMessage());
        }
    }
}

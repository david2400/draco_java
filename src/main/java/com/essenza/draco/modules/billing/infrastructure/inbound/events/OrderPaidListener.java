package com.essenza.draco.modules.billing.infrastructure.inbound.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.essenza.draco.modules.billing.application.input.invoice.AutoInvoiceUseCase;
import com.essenza.draco.shared.common.sales.OrderPaidEvent;
import com.essenza.draco.shared.common.sales.PosSaleCompletedEvent;

/** Tras confirmar el pago de una orden intenta la factura automática; un fallo no afecta la venta. */
@Component
public class OrderPaidListener {

    private static final Logger log = LoggerFactory.getLogger(OrderPaidListener.class);

    private final AutoInvoiceUseCase autoInvoice;

    public OrderPaidListener(AutoInvoiceUseCase autoInvoice) {
        this.autoInvoice = autoInvoice;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderPaid(OrderPaidEvent event) {
        try {
            autoInvoice.onOrderPaid(event.orderId());
        } catch (RuntimeException ex) {
            log.warn("No se pudo facturar automáticamente la orden {}: {}", event.orderId(), ex.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onPosSale(PosSaleCompletedEvent event) {
        try {
            autoInvoice.onPosSale(event);
        } catch (RuntimeException ex) {
            log.warn("No se pudo facturar la venta de mostrador {}: {}", event.orderId(), ex.getMessage());
        }
    }
}

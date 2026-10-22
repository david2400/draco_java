package com.essenza.draco.modules.billing.application.input.invoice;

import com.essenza.draco.shared.common.sales.PosSaleCompletedEvent;

public interface AutoInvoiceUseCase {
    /** Factura a consumidor final una orden recién pagada si la facturación automática está activa (no POS). */
    void onOrderPaid(Long orderId);

    /** Venta de mostrador: factura si la caja o la configuración lo piden, con el comprador si llegó. */
    void onPosSale(PosSaleCompletedEvent event);
}

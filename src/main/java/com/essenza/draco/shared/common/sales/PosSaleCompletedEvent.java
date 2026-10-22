package com.essenza.draco.shared.common.sales;

/**
 * Venta de mostrador cobrada (se publica dentro de la transacción; escuchar tras el commit).
 * {@code autoInvoice}: la caja emite factura electrónica en cada venta. Comprador vacío = consumidor final.
 */
public record PosSaleCompletedEvent(Long orderId, Long cashRegisterId, boolean autoInvoice, String buyerDocType,
                                    String buyerDocNumber, String buyerName, String buyerEmail, String buyerPhone) {
}

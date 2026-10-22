package com.essenza.draco.modules.sales.application.dto.order;

/**
 * Filtros del listado de órdenes.
 *
 * @param query         número, cliente, documento, correo, teléfono, referencia o guía
 * @param paymentStatus UNPAID, PARTIAL o PAID (según los pagos aprobados)
 * @param period        TODAY, LAST_7, LAST_30 o THIS_MONTH (fecha de creación)
 * @param overdue       true = fecha prometida vencida y sin entregar
 */
public record OrderFilter(String query, String state, String channel, Long storeId, String priority, String deliveryMethod,
                          String paymentStatus, String period, Boolean overdue) {

    public static final OrderFilter NONE = new OrderFilter(null, null, null, null, null, null, null, null, null);
}

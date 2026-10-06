package com.essenza.draco.shared.common.inventory;

import java.time.Instant;
import java.util.List;

/**
 * Contrato entre ventas e inventario para reservar, descontar y liberar stock de
 * las órdenes. Se ejecuta dentro de la transacción de ventas. Lo implementa el inventario.
 */
public interface StockReservations {

    /**
     * Deja la reserva de la línea en {@code quantity} unidades del SKU (0 la libera).
     * Reserva primero en la bodega principal y luego en las demás.
     *
     * @throws com.essenza.draco.shared.exceptions.ConflictException si no hay disponible suficiente
     */
    void reserveLine(Long orderId, Long lineId, Long productId, Long skuId, int quantity, Instant expiresAt);

    /** Descuenta del inventario lo reservado por la orden (salida "ORDER" en el kardex). */
    void commitOrder(Long orderId);

    /** Libera lo reservado por la orden; {@code expired} marca la reserva como vencida. */
    void releaseOrder(Long orderId, boolean expired);

    /** Devuelve al inventario lo descontado por una orden pagada que se cancela (entrada "ORDER_CANCEL"). */
    void restockOrder(Long orderId);

    /** Reservas de la orden (todas, con su estado). */
    List<ReservationLine> reservations(Long orderId);

    record ReservationLine(Long id, Long lineId, Long skuId, Long warehouseId, int quantity, String status,
                           Instant expiresAt) {
    }
}

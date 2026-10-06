package com.essenza.draco.modules.inventory.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Unidades de un SKU apartadas en una bodega para una línea de orden.
 * ACTIVE → COMMITTED (pagada) | RELEASED (cancelada/borrada) | EXPIRED (vencida);
 * COMMITTED → RETURNED (orden pagada que se cancela).
 */
public final class StockReservation {

    public enum Status { ACTIVE, COMMITTED, RELEASED, EXPIRED, RETURNED }

    private final Long id;
    private final Long orderId;
    private final Long lineId;
    private final Long skuId;
    private final Long productId;
    private final Long warehouseId;
    private int quantity;
    private Status status;
    private Instant expiresAt;

    public StockReservation(Long id, Long orderId, Long lineId, Long skuId, Long productId, Long warehouseId,
                            int quantity, Status status, Instant expiresAt) {
        this.id = id;
        this.orderId = Objects.requireNonNull(orderId, "orderId");
        this.lineId = Objects.requireNonNull(lineId, "lineId");
        this.skuId = Objects.requireNonNull(skuId, "skuId");
        this.productId = Objects.requireNonNull(productId, "productId");
        this.warehouseId = Objects.requireNonNull(warehouseId, "warehouseId");
        if (quantity < 0) {
            throw new IllegalArgumentException("La cantidad reservada no puede ser negativa");
        }
        this.quantity = quantity;
        this.status = Objects.requireNonNull(status, "status");
        this.expiresAt = expiresAt;
    }

    public static StockReservation open(Long orderId, Long lineId, Long skuId, Long productId, Long warehouseId,
                                        int quantity, Instant expiresAt) {
        return new StockReservation(null, orderId, lineId, skuId, productId, warehouseId, quantity, Status.ACTIVE, expiresAt);
    }

    public void increase(int units) {
        requireActive();
        quantity += units;
    }

    /** Reduce lo reservado; al llegar a cero queda liberada. */
    public void decrease(int units) {
        requireActive();
        if (units > quantity) {
            throw new IllegalArgumentException("No se puede liberar más de lo reservado");
        }
        quantity -= units;
        if (quantity == 0) {
            status = Status.RELEASED;
        }
    }

    public void commit() {
        requireActive();
        status = Status.COMMITTED;
    }

    public void release(boolean expired) {
        requireActive();
        status = expired ? Status.EXPIRED : Status.RELEASED;
    }

    public void markReturned() {
        if (status != Status.COMMITTED) {
            throw new IllegalStateException("Solo se devuelve stock ya descontado");
        }
        status = Status.RETURNED;
    }

    public void extendUntil(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    private void requireActive() {
        if (status != Status.ACTIVE) {
            throw new IllegalStateException("La reserva " + id + " ya no está activa (" + status + ")");
        }
    }

    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getLineId() { return lineId; }
    public Long getSkuId() { return skuId; }
    public Long getProductId() { return productId; }
    public Long getWarehouseId() { return warehouseId; }
    public int getQuantity() { return quantity; }
    public Status getStatus() { return status; }
    public Instant getExpiresAt() { return expiresAt; }
}

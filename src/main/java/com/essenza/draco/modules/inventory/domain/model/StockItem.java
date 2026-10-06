package com.essenza.draco.modules.inventory.domain.model;

import java.util.Objects;

/**
 * Existencias de un SKU en una bodega (agregado del inventario).
 *
 * <ul>
 *   <li>{@code onHand}: unidades físicas.</li>
 *   <li>{@code reserved}: comprometidas para órdenes (se usará en la Fase 5).</li>
 *   <li>{@code available} = onHand − reserved: lo que se puede vender o mover.</li>
 * </ul>
 * Invariantes: nada negativo y reserved ≤ onHand.
 */
public final class StockItem {

    private final Long id;
    private final Long skuId;
    private final Long productId;
    private final Long warehouseId;
    private int onHand;
    private int reserved;
    private int minThreshold;

    public StockItem(Long id, Long skuId, Long productId, Long warehouseId, int onHand, int reserved, int minThreshold) {
        this.id = id;
        this.skuId = Objects.requireNonNull(skuId, "skuId es obligatorio");
        this.productId = Objects.requireNonNull(productId, "productId es obligatorio");
        this.warehouseId = Objects.requireNonNull(warehouseId, "warehouseId es obligatorio");
        if (onHand < 0 || reserved < 0 || minThreshold < 0) {
            throw new IllegalArgumentException("Las cantidades de stock no pueden ser negativas");
        }
        if (reserved > onHand) {
            throw new IllegalArgumentException("Las unidades reservadas no pueden superar las físicas");
        }
        this.onHand = onHand;
        this.reserved = reserved;
        this.minThreshold = minThreshold;
    }

    public static StockItem empty(Long skuId, Long productId, Long warehouseId) {
        return new StockItem(null, skuId, productId, warehouseId, 0, 0, 0);
    }

    /** Entrada de mercancía. */
    public void receive(int quantity) {
        requirePositive(quantity);
        onHand += quantity;
    }

    /** Salida de mercancía: solo de lo disponible (no se toca lo reservado). */
    public void issue(int quantity) {
        requirePositive(quantity);
        if (quantity > available()) {
            throw new InsufficientStockException(available(), quantity);
        }
        onHand -= quantity;
    }

    /** Aparta unidades disponibles para una orden. */
    public void reserve(int quantity) {
        requirePositive(quantity);
        if (quantity > available()) {
            throw new InsufficientStockException(available(), quantity);
        }
        reserved += quantity;
    }

    /** Devuelve a disponible unidades que estaban apartadas. */
    public void unreserve(int quantity) {
        requirePositive(quantity);
        if (quantity > reserved) {
            throw new IllegalStateException("No se pueden liberar " + quantity + " unidades: solo hay " + reserved + " reservadas");
        }
        reserved -= quantity;
    }

    /** Salida de unidades reservadas (venta pagada): baja lo físico y lo reservado. */
    public void commitReserved(int quantity) {
        requirePositive(quantity);
        if (quantity > reserved) {
            throw new IllegalStateException("No se pueden descontar " + quantity + " unidades: solo hay " + reserved + " reservadas");
        }
        reserved -= quantity;
        onHand -= quantity;
    }

    public int available() {
        return onHand - reserved;
    }

    public boolean isBelowThreshold() {
        return minThreshold > 0 && available() <= minThreshold;
    }

    private static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
    }

    public Long getId() { return id; }
    public Long getSkuId() { return skuId; }
    public Long getProductId() { return productId; }
    public Long getWarehouseId() { return warehouseId; }
    public int getOnHand() { return onHand; }
    public int getReserved() { return reserved; }
    public int getMinThreshold() { return minThreshold; }
}

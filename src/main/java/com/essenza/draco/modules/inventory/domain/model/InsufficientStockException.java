package com.essenza.draco.modules.inventory.domain.model;

/** No hay unidades disponibles suficientes en la bodega para la operación. */
public class InsufficientStockException extends RuntimeException {

    private final int available;
    private final int requested;

    public InsufficientStockException(int available, int requested) {
        super("Stock insuficiente: disponibles " + available + ", solicitadas " + requested);
        this.available = available;
        this.requested = requested;
    }

    public int getAvailable() {
        return available;
    }

    public int getRequested() {
        return requested;
    }
}

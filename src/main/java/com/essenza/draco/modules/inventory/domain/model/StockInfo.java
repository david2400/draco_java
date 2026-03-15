package com.essenza.draco.modules.inventory.domain.model;

import java.util.Objects;

public final class StockInfo {
    private final int onHand;
    private final int reserved;

    private StockInfo(int onHand, int reserved) {
        if (onHand < 0 || reserved < 0) {
            throw new IllegalArgumentException("Stock values cannot be negative");
        }
        if (reserved > onHand) {
            throw new IllegalArgumentException("Reserved stock cannot exceed on-hand units");
        }
        this.onHand = onHand;
        this.reserved = reserved;
    }

    public static StockInfo of(int onHand, int reserved) {
        return new StockInfo(onHand, reserved);
    }

    public int getOnHand() {
        return onHand;
    }

    public int getReserved() {
        return reserved;
    }

    public int getAvailableToSell() {
        return onHand - reserved;
    }

    public StockInfo add(int increment) {
        if (increment < 0) {
            throw new IllegalArgumentException("Increment must be positive");
        }
        return new StockInfo(onHand + increment, reserved);
    }

    public StockInfo reserve(int quantity) {
        if (quantity < 0 || quantity > getAvailableToSell()) {
            throw new IllegalArgumentException("Invalid reservation amount");
        }
        return new StockInfo(onHand, reserved + quantity);
    }

    public StockInfo release(int quantity) {
        if (quantity < 0 || quantity > reserved) {
            throw new IllegalArgumentException("Invalid release amount");
        }
        return new StockInfo(onHand, reserved - quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockInfo stockInfo)) return false;
        return onHand == stockInfo.onHand && reserved == stockInfo.reserved;
    }

    @Override
    public int hashCode() {
        return Objects.hash(onHand, reserved);
    }
}

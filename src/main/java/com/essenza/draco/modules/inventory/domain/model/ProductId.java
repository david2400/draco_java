package com.essenza.draco.modules.inventory.domain.model;

public final class ProductId {
    private final Long value;

    private ProductId(Long value) {
        this.value = value;
    }

    public static ProductId of(Long value) {
        if (value == null) {
            return new ProductId(null);
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Product id must be positive");
        }
        return new ProductId(value);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductId productId = (ProductId) o;
        return value.equals(productId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "ProductId{" + "value=" + value + '}';
    }
}

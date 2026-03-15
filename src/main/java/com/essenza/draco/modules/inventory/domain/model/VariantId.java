package com.essenza.draco.modules.inventory.domain.model;

import java.util.Objects;

public final class VariantId {
    private final Long value;

    private VariantId(Long value) {
        this.value = Objects.requireNonNull(value, "Variant id cannot be null");
    }

    public static VariantId of(Long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Variant id must be positive");
        }
        return new VariantId(value);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariantId variantId = (VariantId) o;
        return value.equals(variantId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "VariantId{" + "value=" + value + '}';
    }
}

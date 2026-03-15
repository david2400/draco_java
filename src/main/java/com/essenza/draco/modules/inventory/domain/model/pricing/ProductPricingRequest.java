package com.essenza.draco.modules.inventory.domain.model.pricing;

import com.essenza.draco.modules.inventory.domain.model.VariantId;

import java.math.BigDecimal;
import java.util.Optional;

public final class ProductPricingRequest {
    private final int quantity;
    private final VariantId variantId;
    private final BigDecimal overrideUnitPrice;

    private ProductPricingRequest(int quantity, VariantId variantId, BigDecimal overrideUnitPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
        this.variantId = variantId;
        this.overrideUnitPrice = overrideUnitPrice;
    }

    public static ProductPricingRequest simple(int quantity) {
        return new ProductPricingRequest(quantity, null, null);
    }

    public static ProductPricingRequest variant(VariantId variantId, int quantity) {
        if (variantId == null) {
            throw new IllegalArgumentException("Variant id is required for variant pricing");
        }
        return new ProductPricingRequest(quantity, variantId, null);
    }

    public static ProductPricingRequest override(int quantity, BigDecimal overrideUnitPrice) {
        if (overrideUnitPrice == null) {
            throw new IllegalArgumentException("Override price cannot be null");
        }
        if (overrideUnitPrice.signum() <= 0) {
            throw new IllegalArgumentException("Override price must be positive");
        }
        return new ProductPricingRequest(quantity, null, overrideUnitPrice);
    }

    public int getQuantity() {
        return quantity;
    }

    public Optional<VariantId> getVariantId() {
        return Optional.ofNullable(variantId);
    }

    public Optional<BigDecimal> getOverrideUnitPrice() {
        return Optional.ofNullable(overrideUnitPrice);
    }
}

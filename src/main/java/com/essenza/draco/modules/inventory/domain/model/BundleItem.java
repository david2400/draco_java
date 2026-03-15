package com.essenza.draco.modules.inventory.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class BundleItem {
    private final ProductId productId;
    private final int quantity;
    private final BigDecimal contributionPercentage;

    private BundleItem(ProductId productId, int quantity, BigDecimal contributionPercentage) {
        this.productId = Objects.requireNonNull(productId, "Product id is required");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Bundle item quantity must be greater than zero");
        }
        this.quantity = quantity;
        this.contributionPercentage = normalizeContribution(contributionPercentage);
    }

    public static BundleItem of(ProductId productId, int quantity) {
        return new BundleItem(productId, quantity, BigDecimal.ONE);
    }

    public static BundleItem weighted(ProductId productId, int quantity, BigDecimal contributionPercentage) {
        return new BundleItem(productId, quantity, contributionPercentage);
    }

    private static BigDecimal normalizeContribution(BigDecimal contributionPercentage) {
        BigDecimal result = contributionPercentage == null ? BigDecimal.ONE : contributionPercentage;
        if (result.compareTo(BigDecimal.ZERO) < 0 || result.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Contribution percentage must be between 0 and 1");
        }
        return result;
    }

    public ProductId getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getContributionPercentage() {
        return contributionPercentage;
    }
}

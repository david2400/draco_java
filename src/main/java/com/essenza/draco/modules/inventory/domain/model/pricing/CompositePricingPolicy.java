package com.essenza.draco.modules.inventory.domain.model.pricing;

import com.essenza.draco.modules.inventory.domain.model.BundleItem;
import com.essenza.draco.modules.inventory.domain.model.Product;

import java.math.BigDecimal;

public final class CompositePricingPolicy implements PricingPolicy {

    private final BundlePriceResolver priceResolver;

    public CompositePricingPolicy(BundlePriceResolver priceResolver) {
        this.priceResolver = priceResolver;
    }

    @Override
    public BigDecimal calculate(Product product, ProductPricingRequest request) {
        return product.getBundleItems().stream()
                .map(item -> priceForItem(item, request.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal priceForItem(BundleItem item, int comboQuantity) {
        BigDecimal itemPrice = priceResolver.priceFor(item.getProductId(), item.getQuantity() * comboQuantity);
        return itemPrice.multiply(item.getContributionPercentage());
    }
}

package com.essenza.draco.modules.inventory.domain.model.pricing;

import com.essenza.draco.modules.inventory.domain.model.Product;

import java.math.BigDecimal;

public final class SimplePricingPolicy implements PricingPolicy {
    @Override
    public BigDecimal calculate(Product product, ProductPricingRequest request) {
        BigDecimal unitPrice = request.getOverrideUnitPrice().orElse(product.getUnitPrice());
        return unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));
    }
}

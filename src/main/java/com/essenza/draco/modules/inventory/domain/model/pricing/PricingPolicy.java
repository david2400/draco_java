package com.essenza.draco.modules.inventory.domain.model.pricing;

import com.essenza.draco.modules.inventory.domain.model.Product;

import java.math.BigDecimal;

@FunctionalInterface
public interface PricingPolicy {
    BigDecimal calculate(Product product, ProductPricingRequest request);
}

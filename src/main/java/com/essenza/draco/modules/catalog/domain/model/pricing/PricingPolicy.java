package com.essenza.draco.modules.catalog.domain.model.pricing;

import com.essenza.draco.modules.catalog.domain.model.Product;

import java.math.BigDecimal;

@FunctionalInterface
public interface PricingPolicy {
    BigDecimal calculate(Product product, ProductPricingRequest request);
}

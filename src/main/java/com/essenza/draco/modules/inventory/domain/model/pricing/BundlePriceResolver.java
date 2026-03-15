package com.essenza.draco.modules.inventory.domain.model.pricing;

import com.essenza.draco.modules.inventory.domain.model.ProductId;

import java.math.BigDecimal;

@FunctionalInterface
public interface BundlePriceResolver {
    BigDecimal priceFor(ProductId productId, int quantity);
}

package com.essenza.draco.modules.catalog.domain.model.pricing;

import com.essenza.draco.modules.catalog.domain.model.ProductId;

import java.math.BigDecimal;

@FunctionalInterface
public interface BundlePriceResolver {
    BigDecimal priceFor(ProductId productId, int quantity);
}

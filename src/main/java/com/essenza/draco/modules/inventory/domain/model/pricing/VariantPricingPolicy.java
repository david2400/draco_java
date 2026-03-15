package com.essenza.draco.modules.inventory.domain.model.pricing;

import com.essenza.draco.modules.inventory.domain.model.Product;
import com.essenza.draco.modules.inventory.domain.model.Variant;
import com.essenza.draco.modules.inventory.domain.model.VariantId;

import java.math.BigDecimal;

public final class VariantPricingPolicy implements PricingPolicy {
    @Override
    public BigDecimal calculate(Product product, ProductPricingRequest request) {
        VariantId variantId = request.getVariantId()
                .orElseThrow(() -> new IllegalArgumentException("Variant id required for variant pricing"));
        Variant variant = product.getVariantById(variantId);
        BigDecimal unitPrice = request.getOverrideUnitPrice().orElse(variant.getUnitPrice());
        return unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));
    }
}

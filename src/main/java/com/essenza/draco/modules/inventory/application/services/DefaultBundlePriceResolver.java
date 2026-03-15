package com.essenza.draco.modules.inventory.application.services;

import com.essenza.draco.modules.inventory.application.output.repository.ProductAggregateRepository;
import com.essenza.draco.modules.inventory.domain.model.Product;
import com.essenza.draco.modules.inventory.domain.model.ProductId;
import com.essenza.draco.modules.inventory.domain.model.pricing.BundlePriceResolver;
import com.essenza.draco.modules.inventory.domain.model.pricing.ProductPricingRequest;
import java.math.BigDecimal;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class DefaultBundlePriceResolver implements BundlePriceResolver {

    private final ObjectProvider<ProductAggregateRepository> aggregateRepositoryProvider;

    public DefaultBundlePriceResolver(ObjectProvider<ProductAggregateRepository> aggregateRepositoryProvider) {
        this.aggregateRepositoryProvider = aggregateRepositoryProvider;
    }

    @Override
    public BigDecimal priceFor(ProductId productId, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Bundle item product id is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Bundle item quantity must be greater than zero");
        }

        Product product = aggregateRepositoryProvider.getObject()
                .findById(productId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Bundle component not found: " + productId));

        return product.calculatePrice(ProductPricingRequest.simple(quantity));
    }
}

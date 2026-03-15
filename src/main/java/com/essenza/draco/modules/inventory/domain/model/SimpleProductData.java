package com.essenza.draco.modules.inventory.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record SimpleProductData(
        ProductId id,
        String sku,
        String name,
        String description,
        StockInfo stockInfo,
        BigDecimal realPrice,
        BigDecimal unitPrice,
        boolean available
) {
    public SimpleProductData {
        Objects.requireNonNull(id, "Product id is required");
        Objects.requireNonNull(sku, "SKU is required");
        Objects.requireNonNull(name, "Product name is required");
        Objects.requireNonNull(stockInfo, "Stock info is required");
        Objects.requireNonNull(realPrice, "Real price is required");
        Objects.requireNonNull(unitPrice, "Unit price is required");
    }
}

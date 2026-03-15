package com.essenza.draco.modules.inventory.domain.services;

import com.essenza.draco.modules.inventory.domain.model.ProductId;
import com.essenza.draco.modules.inventory.domain.model.ProductType;
import com.essenza.draco.modules.inventory.domain.model.StockInfo;

import java.math.BigDecimal;
import java.util.Objects;

public record ProductDraft(
        ProductId id,
        String name,
        String description,
        ProductType type,
        StockInfo stockInfo,
        BigDecimal realPrice,
        BigDecimal unitPrice,
        Double length,
        Double width,
        Double height,
        Double weight,
        String imageUrl,
        Long brandId,
        Long categoryId,
        Long subcategoryId,
        Long supplierId,
        boolean available
) {
    public ProductDraft {
        Objects.requireNonNull(name, "Product name is required");
        Objects.requireNonNull(type, "Product type is required");
        Objects.requireNonNull(stockInfo, "Stock info is required");
        Objects.requireNonNull(realPrice, "Real price is required");
        Objects.requireNonNull(unitPrice, "Unit price is required");
        Objects.requireNonNull(brandId, "Brand id is required");
        Objects.requireNonNull(categoryId, "Category id is required");
        Objects.requireNonNull(subcategoryId, "Subcategory id is required");
        Objects.requireNonNull(supplierId, "Supplier id is required");
    }
}

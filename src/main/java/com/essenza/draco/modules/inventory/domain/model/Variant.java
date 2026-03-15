package com.essenza.draco.modules.inventory.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Variant {
    private final VariantId id;
    private final String name;
    private final String description;
    private final StockInfo stockInfo;
    private final BigDecimal unitPrice;
    private final String imageUrl;
    private final boolean available;

    private Variant(Builder builder) {
        this.id = builder.id;
        this.name = Objects.requireNonNull(builder.name, "Variant name is required");
        this.description = builder.description;
        this.stockInfo = Objects.requireNonNull(builder.stockInfo, "Variant stock information is required");
        this.unitPrice = Objects.requireNonNull(builder.unitPrice, "Variant unit price is required");
        this.imageUrl = builder.imageUrl;
        this.available = builder.available;
    }

    public static Builder builder(VariantId id) {
        return new Builder(id);
    }

    public VariantId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public StockInfo getStockInfo() {
        return stockInfo;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isAvailable() {
        return available && stockInfo.getAvailableToSell() > 0;
    }

    public Variant withStock(StockInfo newStock) {
        return builder(id)
                .name(name)
                .description(description)
                .stockInfo(newStock)
                .unitPrice(unitPrice)
                .imageUrl(imageUrl)
                .available(available)
                .build();
    }

    public static final class Builder {
        private final VariantId id;
        private String name;
        private String description;
        private StockInfo stockInfo;
        private BigDecimal unitPrice;
        private String imageUrl;
        private boolean available = true;

        private Builder(VariantId id) {
            this.id = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder stockInfo(StockInfo stockInfo) {
            this.stockInfo = stockInfo;
            return this;
        }

        public Builder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        public Variant build() {
            return new Variant(this);
        }
    }
}

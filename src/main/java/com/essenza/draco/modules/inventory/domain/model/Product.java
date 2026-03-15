package com.essenza.draco.modules.inventory.domain.model;

import com.essenza.draco.modules.inventory.domain.model.pricing.PricingPolicy;
import com.essenza.draco.modules.inventory.domain.model.pricing.ProductPricingRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Product {
    private final ProductId id;
    private final String name;
    private final String description;
    private final ProductType type;
    private final StockInfo stockInfo;
    private final BigDecimal realPrice;
    private final BigDecimal unitPrice;
    private final Double length;
    private final Double width;
    private final Double height;
    private final Double weight;
    private final String imageUrl;
    private final Long brandId;
    private final Long categoryId;
    private final Long subcategoryId;
    private final Long supplierId;
    private final boolean available;
    private final List<Variant> variants;
    private final List<BundleItem> bundleItems;
    private final PricingPolicy pricingPolicy;

    private Product(Builder builder) {
        this.id = builder.id;
        this.name = Objects.requireNonNull(builder.name, "Product name is required");
        this.description = builder.description;
        this.type = Objects.requireNonNull(builder.type, "Product type is required");
        this.stockInfo = Objects.requireNonNull(builder.stockInfo, "Stock info is required");
        this.realPrice = Objects.requireNonNull(builder.realPrice, "Real price is required");
        this.unitPrice = Objects.requireNonNull(builder.unitPrice, "Unit price is required");
        this.length = builder.length;
        this.width = builder.width;
        this.height = builder.height;
        this.weight = builder.weight;
        this.imageUrl = builder.imageUrl;
        this.brandId = builder.brandId;
        this.categoryId = builder.categoryId;
        this.subcategoryId = builder.subcategoryId;
        this.supplierId = builder.supplierId;
        this.available = builder.available;
        this.variants = List.copyOf(builder.variants);
        this.bundleItems = List.copyOf(builder.bundleItems);
        this.pricingPolicy = Objects.requireNonNull(builder.pricingPolicy, "Pricing policy is required");
    }

    public ProductId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProductType getType() {
        return type;
    }

    public StockInfo getStockInfo() {
        return stockInfo;
    }

    public BigDecimal getRealPrice() {
        return realPrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Double getLength() {
        return length;
    }

    public Double getWidth() {
        return width;
    }

    public Double getHeight() {
        return height;
    }

    public Double getWeight() {
        return weight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Long getBrandId() {
        return brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getSubcategoryId() {
        return subcategoryId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public boolean isAvailable() {
        if (!available) {
            return false;
        }
        return switch (type) {
            case SIMPLE -> stockInfo.getAvailableToSell() > 0;
            case VARIANT -> variants.stream().anyMatch(Variant::isAvailable);
            case COMBO -> bundleItems.stream().allMatch(item -> true); // availability validated at service layer
        };
    }

    public List<Variant> getVariants() {
        return Collections.unmodifiableList(variants);
    }

    public List<BundleItem> getBundleItems() {
        return Collections.unmodifiableList(bundleItems);
    }

    public BigDecimal calculatePrice(ProductPricingRequest request) {
        return pricingPolicy.calculate(this, request);
    }

    public Variant getVariantById(VariantId variantId) {
        return variants.stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + variantId));
    }

    public Product withVariants(List<Variant> newVariants) {
        ensureType(ProductType.VARIANT);
        return toBuilder().variants(newVariants).build();
    }

    public Product withBundleItems(List<BundleItem> newItems) {
        ensureType(ProductType.COMBO);
        return toBuilder().bundleItems(newItems).build();
    }

    private void ensureType(ProductType expected) {
        if (type != expected) {
            throw new IllegalStateException("Operation allowed only for " + expected + " products");
        }
    }

    public Builder toBuilder() {
        return new Builder(id)
                .name(name)
                .description(description)
                .type(type)
                .stockInfo(stockInfo)
                .realPrice(realPrice)
                .unitPrice(unitPrice)
                .length(length)
                .width(width)
                .height(height)
                .weight(weight)
                .imageUrl(imageUrl)
                .brandId(brandId)
                .categoryId(categoryId)
                .subcategoryId(subcategoryId)
                .supplierId(supplierId)
                .available(available)
                .variants(variants)
                .bundleItems(bundleItems)
                .pricingPolicy(pricingPolicy);
    }

    public static Builder builder(ProductId id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final ProductId id;
        private String name;
        private String description;
        private ProductType type = ProductType.SIMPLE;
        private StockInfo stockInfo;
        private BigDecimal realPrice;
        private BigDecimal unitPrice;
        private Double length;
        private Double width;
        private Double height;
        private Double weight;
        private String imageUrl;
        private Long brandId;
        private Long categoryId;
        private Long subcategoryId;
        private Long supplierId;
        private boolean available = true;
        private List<Variant> variants = new ArrayList<>();
        private List<BundleItem> bundleItems = new ArrayList<>();
        private PricingPolicy pricingPolicy;

        private Builder(ProductId id) {
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

        public Builder type(ProductType type) {
            this.type = type;
            return this;
        }

        public Builder stockInfo(StockInfo stockInfo) {
            this.stockInfo = stockInfo;
            return this;
        }

        public Builder realPrice(BigDecimal realPrice) {
            this.realPrice = realPrice;
            return this;
        }

        public Builder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder length(Double length) {
            this.length = length;
            return this;
        }

        public Builder width(Double width) {
            this.width = width;
            return this;
        }

        public Builder height(Double height) {
            this.height = height;
            return this;
        }

        public Builder weight(Double weight) {
            this.weight = weight;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder brandId(Long brandId) {
            this.brandId = brandId;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder subcategoryId(Long subcategoryId) {
            this.subcategoryId = subcategoryId;
            return this;
        }

        public Builder supplierId(Long supplierId) {
            this.supplierId = supplierId;
            return this;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        public Builder variants(List<Variant> variants) {
            this.variants = new ArrayList<>(Optional.ofNullable(variants).orElseGet(List::of));
            return this;
        }

        public Builder bundleItems(List<BundleItem> bundleItems) {
            this.bundleItems = new ArrayList<>(Optional.ofNullable(bundleItems).orElseGet(List::of));
            return this;
        }

        public Builder pricingPolicy(PricingPolicy pricingPolicy) {
            this.pricingPolicy = pricingPolicy;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}

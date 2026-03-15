package com.essenza.draco.modules.inventory.domain.services;

import com.essenza.draco.modules.inventory.domain.command.ProductBundleItemCommand;
import com.essenza.draco.modules.inventory.domain.command.ProductCommand;
import com.essenza.draco.modules.inventory.domain.command.ProductVariantCommand;
import com.essenza.draco.modules.inventory.domain.model.BundleItem;
import com.essenza.draco.modules.inventory.domain.model.Product;
import com.essenza.draco.modules.inventory.domain.model.ProductId;
import com.essenza.draco.modules.inventory.domain.model.ProductType;
import com.essenza.draco.modules.inventory.domain.model.StockInfo;
import com.essenza.draco.modules.inventory.domain.model.Variant;
import com.essenza.draco.modules.inventory.domain.model.VariantId;
import com.essenza.draco.modules.inventory.domain.model.pricing.BundlePriceResolver;
import com.essenza.draco.modules.inventory.domain.model.pricing.CompositePricingPolicy;
import com.essenza.draco.modules.inventory.domain.model.pricing.PricingPolicy;
import com.essenza.draco.modules.inventory.domain.model.pricing.SimplePricingPolicy;
import com.essenza.draco.modules.inventory.domain.model.pricing.VariantPricingPolicy;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class ProductFactory {

    private final BundlePriceResolver bundlePriceResolver;

    public ProductFactory(BundlePriceResolver bundlePriceResolver) {
        this.bundlePriceResolver = Objects.requireNonNull(bundlePriceResolver, "Bundle price resolver is required");
    }

    public Product fromDraft(ProductDraft draft, List<Variant> variants, List<BundleItem> bundleItems) {
        Objects.requireNonNull(draft, "Product draft is required");
        List<Variant> safeVariants = variants == null ? List.of() : List.copyOf(variants);
        List<BundleItem> safeBundleItems = bundleItems == null ? List.of() : List.copyOf(bundleItems);

        PricingPolicy pricingPolicy = resolvePolicy(draft.type());

        return Product.builder(draft.id())
                .name(draft.name())
                .description(draft.description())
                .type(draft.type())
                .stockInfo(draft.stockInfo())
                .realPrice(draft.realPrice())
                .unitPrice(draft.unitPrice())
                .length(draft.length())
                .width(draft.width())
                .height(draft.height())
                .weight(draft.weight())
                .imageUrl(draft.imageUrl())
                .brandId(draft.brandId())
                .categoryId(draft.categoryId())
                .subcategoryId(draft.subcategoryId())
                .supplierId(draft.supplierId())
                .available(draft.available())
                .variants(variantsForType(draft.type(), safeVariants))
                .bundleItems(bundleItemsForType(draft.type(), safeBundleItems))
                .pricingPolicy(pricingPolicy)
                .build();
    }

    public Product fromCommand(ProductCommand command) {
        Objects.requireNonNull(command, "Product command is required");

        ProductDraft draft = new ProductDraft(
                command.getId() == null ? null : ProductId.of(command.getId()),
                command.getName(),
                command.getDescription(),
                resolveType(command),
                StockInfo.of(nullSafeInt(command.getStock()), 0),
                command.getRealPrice(),
                command.getUnitPrice(),
                command.getLength(),
                command.getWidth(),
                command.getHeight(),
                command.getWeight(),
                command.getImageUrl(),
                command.getBrandId(),
                command.getCategoryId(),
                command.getSubcategoryId(),
                command.getSupplierId(),
                Boolean.TRUE.equals(command.getAvailable())
        );

        List<Variant> variants = Optional.ofNullable(command.getVariants())
                .orElse(List.of())
                .stream()
                .map(this::toVariant)
                .toList();

        List<BundleItem> bundleItems = Optional.ofNullable(command.getBundleItems())
                .orElse(List.of())
                .stream()
                .map(this::toBundleItem)
                .toList();

        return fromDraft(draft, variants, bundleItems);
    }

    private ProductType resolveType(ProductCommand command) {
        if (Boolean.TRUE.equals(command.getIsCombo())) {
            return ProductType.COMBO;
        }
        boolean hasVariants = command.getVariants() != null && !command.getVariants().isEmpty();
        return hasVariants ? ProductType.VARIANT : ProductType.SIMPLE;
    }

    private Variant toVariant(ProductVariantCommand variantCommand) {
        VariantId variantId = variantCommand.getId() == null
                ? null
                : VariantId.of(variantCommand.getId());
        return Variant.builder(variantId)
                .name(variantCommand.getName())
                .description(variantCommand.getDescription())
                .stockInfo(StockInfo.of(nullSafeInt(variantCommand.getStock()), 0))
                .unitPrice(Optional.ofNullable(variantCommand.getUnitPrice()).orElse(BigDecimal.ZERO))
                .imageUrl(variantCommand.getImageUrl())
                .available(Boolean.TRUE.equals(variantCommand.getAvailable()))
                .build();
    }

    private BundleItem toBundleItem(ProductBundleItemCommand bundleCommand) {
        return bundleCommand.getContributionPercentage() == null
                ? BundleItem.of(ProductId.of(bundleCommand.getProductId()), bundleCommand.getQuantity())
                : BundleItem.weighted(
                ProductId.of(bundleCommand.getProductId()),
                bundleCommand.getQuantity(),
                bundleCommand.getContributionPercentage());
    }

    private int nullSafeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private PricingPolicy resolvePolicy(ProductType type) {
        return switch (type) {
            case SIMPLE -> new SimplePricingPolicy();
            case VARIANT -> new VariantPricingPolicy();
            case COMBO -> new CompositePricingPolicy(bundlePriceResolver);
        };
    }

    private List<Variant> variantsForType(ProductType type, List<Variant> variants) {
        if (type.supportsVariants()) {
            return variants;
        }
        if (!variants.isEmpty()) {
            throw new IllegalArgumentException("Variants are only allowed for VARIANT products");
        }
        return Collections.emptyList();
    }

    private List<BundleItem> bundleItemsForType(ProductType type, List<BundleItem> bundleItems) {
        if (type.supportsBundles()) {
            if (bundleItems.isEmpty()) {
                throw new IllegalArgumentException("Composite products require bundle items");
            }
            return bundleItems;
        }
        if (!bundleItems.isEmpty()) {
            throw new IllegalArgumentException("Bundle items are only allowed for COMBO products");
        }
        return Collections.emptyList();
    }
}

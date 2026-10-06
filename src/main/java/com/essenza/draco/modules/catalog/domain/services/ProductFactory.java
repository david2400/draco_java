package com.essenza.draco.modules.catalog.domain.services;

import com.essenza.draco.modules.catalog.domain.command.ProductBundleItemCommand;
import com.essenza.draco.modules.catalog.domain.command.ProductCommand;
import com.essenza.draco.modules.catalog.domain.command.ProductVariantCommand;
import com.essenza.draco.modules.catalog.domain.model.BundleItem;
import com.essenza.draco.modules.catalog.domain.model.Product;
import com.essenza.draco.modules.catalog.domain.model.ProductId;
import com.essenza.draco.modules.catalog.domain.model.ProductStatus;
import com.essenza.draco.modules.catalog.domain.model.ProductType;
import com.essenza.draco.modules.catalog.domain.model.StockInfo;
import com.essenza.draco.modules.catalog.domain.model.Variant;
import com.essenza.draco.modules.catalog.domain.model.VariantId;
import com.essenza.draco.modules.catalog.domain.model.pricing.BundlePriceResolver;
import com.essenza.draco.modules.catalog.domain.model.pricing.CompositePricingPolicy;
import com.essenza.draco.modules.catalog.domain.model.pricing.PricingPolicy;
import com.essenza.draco.modules.catalog.domain.model.pricing.SimplePricingPolicy;
import com.essenza.draco.modules.catalog.domain.model.pricing.VariantPricingPolicy;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Fábrica del agregado {@link Product}. Java puro: se registra como bean en
 * {@code catalog.infrastructure.config.CatalogDomainConfig}.
 */
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
                .status(draft.status())
                .slug(draft.slug())
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
                Boolean.TRUE.equals(command.getAvailable()),
                ProductStatus.parse(command.getStatus()),
                command.getSlug()
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

        if (draft.type() == ProductType.COMBO && bundleItems.isEmpty()) {
            throw new IllegalArgumentException("Composite products require bundle items");
        }
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
            // Al rehidratar se toleran combos heredados sin componentes (no rompen el
            // listado); la regla se exige al crear/editar en fromCommand.
            return bundleItems;
        }
        if (!bundleItems.isEmpty()) {
            throw new IllegalArgumentException("Bundle items are only allowed for COMBO products");
        }
        return Collections.emptyList();
    }
}

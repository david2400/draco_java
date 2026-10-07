package com.essenza.draco.modules.catalog.application.services;

import com.essenza.draco.modules.catalog.domain.command.ProductBundleItemCommand;
import com.essenza.draco.modules.catalog.domain.command.ProductCommand;
import com.essenza.draco.modules.catalog.domain.command.ProductVariantCommand;
import com.essenza.draco.modules.catalog.application.dto.product.CreateProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductBundleItemDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductVariantDto;
import com.essenza.draco.modules.catalog.application.dto.product.UpdateProductDto;
import com.essenza.draco.modules.catalog.domain.model.Product;
import com.essenza.draco.modules.catalog.domain.model.ProductStatus;
import com.essenza.draco.modules.catalog.domain.model.ProductType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductCommandAssembler {

    public ProductCommand fromCreateDto(CreateProductDto dto) {
        ProductCommand base = toCommand(dto, null);
        // Alta sin estado explícito: publicado si llega available=true, si no borrador.
        ProductStatus status = ProductStatus.parse(dto.getStatus());
        if (status == null) {
            status = Boolean.FALSE.equals(dto.getAvailable()) ? ProductStatus.DRAFT : ProductStatus.ACTIVE;
        }
        return base.toBuilder()
                .status(status.name())
                .available(status.isListed())
                .build();
    }

    /**
     * Comando de actualización. El id sale de la URL; si el payload no trae
     * {@code variants} o {@code bundleItems} se conservan los del producto actual,
     * para que un formulario que solo edita la ficha no borre las variantes.
     */
    public ProductCommand fromUpdateDto(Long id, UpdateProductDto dto, Product current) {
        ProductCommand base = toCommand(dto, id);
        boolean keepCombo = dto.getIsCombo() == null && current.getType() == ProductType.COMBO;
        boolean isCombo = Boolean.TRUE.equals(dto.getIsCombo()) || keepCombo;
        List<ProductVariantCommand> variants = dto.getVariants() == null
                ? (isCombo ? List.of() : variantsOf(current))
                : base.getVariants();
        List<ProductBundleItemCommand> bundleItems = dto.getBundleItems() == null
                ? (isCombo ? bundleItemsOf(current) : List.of())
                : base.getBundleItems();
        ProductStatus status = ProductStatus.parse(dto.getStatus());
        // Clientes que reenvían el producto leído (GET) y solo cambian "available":
        // el estado llega igual al actual pero contradice available => manda available.
        if (status != null && status == current.getStatus() && dto.getAvailable() != null
                && dto.getAvailable() != status.isListed()) {
            status = null;
        }
        if (status == null) {
            status = dto.getAvailable() == null
                    ? current.getStatus()
                    : ProductStatus.fromAvailability(dto.getAvailable(), current.getStatus());
        }
        String slug = dto.getSlug() == null || dto.getSlug().isBlank() ? current.getSlug() : dto.getSlug();
        return base.toBuilder()
                .status(status.name())
                .available(status.isListed())
                .slug(slug)
                .isCombo(isCombo)
                .variants(variants)
                .bundleItems(bundleItems)
                .build();
    }

    private List<ProductVariantCommand> variantsOf(Product current) {
        return current.getVariants().stream()
                .map(variant -> ProductVariantCommand.builder()
                        .id(variant.getId() == null ? null : variant.getId().getValue())
                        .name(variant.getName())
                        .description(variant.getDescription())
                        .stock(variant.getStockInfo().getOnHand())
                        .unitPrice(variant.getUnitPrice())
                        .imageUrl(variant.getImageUrl())
                        .available(variant.isListed())
                        .build())
                .toList();
    }

    private List<ProductBundleItemCommand> bundleItemsOf(Product current) {
        return current.getBundleItems().stream()
                .map(item -> ProductBundleItemCommand.builder()
                        .productId(item.getProductId().getValue())
                        .quantity(item.getQuantity())
                        .contributionPercentage(item.getContributionPercentage())
                        .build())
                .toList();
    }

    private ProductCommand toCommand(CreateProductDto dto, Long id) {
        return ProductCommand.builder()
                .id(id)
                .name(dto.getName())
                .description(dto.getDescription())
                .stock(dto.getStock())
                .realPrice(dto.getRealPrice())
                .unitPrice(dto.getUnitPrice())
                .length(dto.getLength())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .weight(dto.getWeight())
                .netContent(dto.getNetContent())
                .netContentUnitId(dto.getNetContentUnitId())
                .imageUrl(dto.getImageUrl())
                .available(dto.getAvailable())
                .status(dto.getStatus())
                .slug(dto.getSlug())
                .brandId(dto.getBrandId())
                .categoryId(dto.getCategoryId())
                .subcategoryId(dto.getSubcategoryId())
                .supplierId(dto.getSupplierId())
                .isCombo(Boolean.TRUE.equals(dto.getIsCombo()))
                .variants(mapVariants(dto.getVariants()))
                .bundleItems(mapBundleItems(dto.getBundleItems()))
                .build();
    }

    private List<ProductVariantCommand> mapVariants(List<ProductVariantDto> variants) {
        if (variants == null || variants.isEmpty()) {
            return List.of();
        }
        return variants.stream()
                .map(variant -> ProductVariantCommand.builder()
                        .id(variant.getId())
                        .name(variant.getName())
                        .description(variant.getDescription())
                        .stock(variant.getStock())
                        .unitPrice(variant.getUnitPrice())
                        .imageUrl(variant.getImageUrl())
                        .available(variant.getAvailable())
                        .build())
                .toList();
    }

    private List<ProductBundleItemCommand> mapBundleItems(List<ProductBundleItemDto> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return items.stream()
                .map(item -> ProductBundleItemCommand.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .contributionPercentage(item.getContributionPercentage())
                        .build())
                .toList();
    }
}

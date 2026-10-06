package com.essenza.draco.modules.catalog.product.support;

import com.essenza.draco.modules.catalog.application.dto.product.ProductVariantDto;
import com.essenza.draco.modules.catalog.application.dto.product.UpdateProductDto;
import com.essenza.draco.modules.catalog.domain.model.BundleItem;
import com.essenza.draco.modules.catalog.domain.model.Product;
import com.essenza.draco.modules.catalog.domain.model.ProductId;
import com.essenza.draco.modules.catalog.domain.model.ProductStatus;
import com.essenza.draco.modules.catalog.domain.model.ProductType;
import com.essenza.draco.modules.catalog.domain.model.StockInfo;
import com.essenza.draco.modules.catalog.domain.model.Variant;
import com.essenza.draco.modules.catalog.domain.model.VariantId;
import com.essenza.draco.modules.catalog.domain.services.ProductDraft;
import com.essenza.draco.modules.catalog.domain.services.ProductFactory;
import java.math.BigDecimal;
import java.util.List;

/** Datos de prueba del agregado Product (sin Spring ni base de datos). */
public final class ProductFixtures {

    public static final ProductFactory FACTORY =
            new ProductFactory((productId, quantity) -> BigDecimal.TEN.multiply(BigDecimal.valueOf(quantity)));

    private ProductFixtures() {
    }

    public static ProductDraft draft(Long id, ProductType type, int stock, boolean available) {
        return new ProductDraft(
                id == null ? null : ProductId.of(id),
                "Perfume Floral",
                "Eau de parfum",
                type,
                StockInfo.of(stock, 0),
                new BigDecimal("80000"),
                new BigDecimal("120000"),
                10.0, 5.0, 15.0, 0.4,
                null,
                1L, 2L, 3L, 4L,
                available,
                available ? ProductStatus.ACTIVE : ProductStatus.INACTIVE,
                "perfume-floral");
    }

    public static Variant variant(Long id, String name, int stock, boolean available) {
        return Variant.builder(id == null ? null : VariantId.of(id))
                .name(name)
                .stockInfo(StockInfo.of(stock, 0))
                .unitPrice(new BigDecimal("99000"))
                .available(available)
                .build();
    }

    public static Product simple(Long id, int stock, boolean available) {
        return FACTORY.fromDraft(draft(id, ProductType.SIMPLE, stock, available), List.of(), List.of());
    }

    public static Product withVariants(Long id, Variant... variants) {
        return FACTORY.fromDraft(draft(id, ProductType.VARIANT, 0, true), List.of(variants), List.of());
    }

    public static Product combo(Long id, BundleItem... items) {
        return FACTORY.fromDraft(draft(id, ProductType.COMBO, 0, true), List.of(), List.of(items));
    }

    /** Payload típico del panel al editar la ficha: SIN id en el body y SIN variantes. */
    public static UpdateProductDto panelUpdate(String name) {
        UpdateProductDto dto = new UpdateProductDto();
        dto.setName(name);
        dto.setDescription("Eau de parfum");
        dto.setStock(7);
        dto.setRealPrice(new BigDecimal("80000"));
        dto.setUnitPrice(new BigDecimal("125000"));
        dto.setLength(10.0);
        dto.setWidth(5.0);
        dto.setHeight(15.0);
        dto.setWeight(0.4);
        dto.setAvailable(true);
        dto.setBrandId(1L);
        dto.setCategoryId(2L);
        dto.setSubcategoryId(3L);
        dto.setSupplierId(4L);
        return dto;
    }

    public static ProductVariantDto variantDto(Long id, String name) {
        return ProductVariantDto.builder()
                .id(id)
                .name(name)
                .stock(3)
                .unitPrice(new BigDecimal("50000"))
                .available(true)
                .build();
    }
}

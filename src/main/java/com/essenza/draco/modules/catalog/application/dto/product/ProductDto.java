package com.essenza.draco.modules.catalog.application.dto.product;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto extends AuditInfoDto {
    private Long id;
    private String name;
    private String description;
    private Integer stock;
    private BigDecimal realPrice;
    private BigDecimal unitPrice;
    private Double length;
    private Double width;
    private Double height;
    private Double weight;
    private String imageUrl;
    private Boolean available;
    private Long brandId;
    private Long categoryId;
    private Long subcategoryId;
    private Long supplierId;
    private Boolean isCombo = false;
    /** Calculado: publicado y con stock (o con alguna variante vendible). Solo lectura. */
    private Boolean sellable;
    /** DRAFT | ACTIVE | INACTIVE | ARCHIVED. */
    private String status;
    private String slug;
    /** SIMPLE | VARIANT | COMBO. */
    private String productType;
    /** Unidades vendibles (Fase 2, solo lectura). Un producto simple tiene un SKU por defecto. */
    private List<ProductSkuDto> skus;
    /** Galería de imágenes (Fase 2, solo lectura). */
    private List<ProductImageDto> images;
    @Builder.Default
    private List<ProductVariantDto> variants = List.of();
    @Builder.Default
    private List<ProductBundleItemDto> bundleItems = List.of();
}

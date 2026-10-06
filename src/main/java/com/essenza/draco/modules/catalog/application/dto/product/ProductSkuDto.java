package com.essenza.draco.modules.catalog.application.dto.product;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** SKU: unidad vendible de un producto (el simple tiene uno; cada variante, el suyo). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSkuDto {
    private Long id;
    private String code;
    private String name;
    private BigDecimal price;
    private BigDecimal costPrice;
    private BigDecimal compareAtPrice;
    private String barcode;
    private String imageUrl;
    private Boolean isDefault;
    private Boolean active;
    /** Id de la variante heredada ({@code product_childs}) de la que proviene, si aplica. */
    private Long variantId;
    /** Unidades físicas del SKU en todas las bodegas (Fase 3, inventario). */
    private Integer stock;
}

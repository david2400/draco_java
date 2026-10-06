package com.essenza.draco.modules.catalog.application.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Imagen de la galería del producto (opcionalmente asociada a un SKU). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
    private Long id;
    private String url;
    private Integer position;
    private String altText;
    private Long skuId;
}

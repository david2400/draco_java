package com.essenza.draco.modules.catalog.application.dto.product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDto {
    private Long id;
    private String name;
    private String description;
    private Integer stock;
    private BigDecimal unitPrice;
    private String imageUrl;
    private Boolean available;
    /** Calculado: activa y con stock. Solo lectura. */
    private Boolean sellable;
}

package com.essenza.draco.modules.catalog.application.dto.lookup;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SKU en formato ligero para selectores con búsqueda (líneas de orden, movimientos).
 *
 * @param name      "Producto · variante" (o solo el producto si no tiene variantes)
 * @param sellable  SKU activo y producto publicado (ACTIVE)
 * @param onHand    unidades físicas en todas las bodegas
 * @param available unidades disponibles para vender (on hand − reservadas)
 */
public record SkuLookupDto(
        Long skuId,
        Long productId,
        String code,
        String name,
        String productName,
        String variantName,
        @Schema(allowableValues = {"SIMPLE", "VARIANT", "COMBO"}) String productType,
        BigDecimal unitPrice,
        boolean active,
        boolean sellable,
        int onHand,
        int available,
        String imageUrl) {

    public SkuLookupDto withStock(int onHand, int available) {
        return new SkuLookupDto(skuId, productId, code, name, productName, variantName, productType, unitPrice,
                active, sellable, onHand, available, imageUrl);
    }
}

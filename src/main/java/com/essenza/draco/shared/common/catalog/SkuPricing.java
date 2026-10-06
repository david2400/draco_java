package com.essenza.draco.shared.common.catalog;

import java.math.BigDecimal;

/**
 * Contrato entre ventas y catálogo: precio y datos vigentes de un SKU para
 * congelarlos en la línea de una orden. Lo implementa el catálogo.
 */
public interface SkuPricing {

    /**
     * @param productId producto (opcional si se indica el SKU)
     * @param skuId     SKU; obligatorio si el producto tiene variantes
     * @throws com.essenza.draco.shared.exceptions.NotFoundException si no existe
     * @throws IllegalArgumentException si falta el SKU de un producto con variantes o no coincide con el producto
     */
    SkuSnapshot resolve(Long productId, Long skuId);

    /**
     * Foto del SKU en el momento de vender.
     *
     * @param name     nombre comercial (producto y, si aplica, variante)
     * @param sellable se puede vender: SKU activo y producto publicado (ACTIVE)
     */
    record SkuSnapshot(Long skuId, Long productId, String code, String name, BigDecimal unitPrice, boolean sellable) {
    }
}

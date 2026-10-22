package com.essenza.draco.modules.inventory.application.dto.warehouse;

import java.math.BigDecimal;

/** Existencias de un SKU en la bodega, valorizadas al costo. */
public record WarehouseStockDto(Long skuId, Long productId, String skuCode, String productName, String skuName,
                                int onHand, int reserved, int available, int minThreshold, BigDecimal unitCost,
                                BigDecimal stockValue, boolean belowMin) {
}

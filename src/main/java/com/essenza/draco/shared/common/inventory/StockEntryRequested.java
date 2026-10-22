package com.essenza.draco.shared.common.inventory;

/**
 * Evento de integración catálogo → inventario: entrada de mercancía de un SKU recién creado
 * (p. ej. stock inicial de las variantes generadas). El inventario registra una ENTRADA en la
 * bodega indicada (o en la principal si viene nula). Síncrono y en la misma transacción.
 */
public record StockEntryRequested(Long productId, Long skuId, Long warehouseId, int quantity, String source) {
}

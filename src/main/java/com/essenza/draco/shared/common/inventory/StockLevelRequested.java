package com.essenza.draco.shared.common.inventory;

/**
 * Evento de integración catálogo → inventario.
 *
 * <p>Los formularios heredados del catálogo (producto y variante) envían un
 * "stock" total. Desde la Fase 3 el dueño del stock es el inventario: el
 * catálogo publica este evento y el inventario registra el ajuste necesario
 * (entrada o salida en la bodega principal) para que el total del SKU sea
 * {@code desiredOnHand}. Si ya coincide, no hace nada.
 */
public record StockLevelRequested(Long productId, Long skuId, int desiredOnHand, String source) {
}

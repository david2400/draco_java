package com.essenza.draco.modules.inventory.application.output.repository;

/**
 * Mantiene las columnas heredadas ({@code products.stock}, {@code product_childs.stock},
 * {@code stock_per_warehouse}) como proyección de {@code stock_items} durante la transición.
 */
public interface LegacyStockProjectionPort {

    void refresh(Long productId);
}

package com.essenza.draco.modules.catalog.application.dto.lookup;

import java.math.BigDecimal;
import java.util.List;

/**
 * Indicadores del catálogo calculados en la base de datos (sin cargar productos).
 *
 * @param total          productos no borrados
 * @param active         productos publicados (ACTIVE)
 * @param outOfStock     productos con stock ≤ 0
 * @param lowStock       productos con 0 &lt; stock ≤ umbral
 * @param inventoryValue Σ stock × precio de costo
 * @param lowStockItems  los productos con menos stock (≤ umbral), de menor a mayor
 */
public record ProductStatsDto(
        long total,
        long active,
        long outOfStock,
        long lowStock,
        int lowStockThreshold,
        BigDecimal inventoryValue,
        List<LowStockItem> lowStockItems) {

    public record LowStockItem(Long id, String name, int stock) {
    }
}

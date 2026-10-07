package com.essenza.draco.modules.catalog.application.input.lookup;

import com.essenza.draco.modules.catalog.application.dto.lookup.ProductStatsDto;

public interface GetProductStatsUseCase {

    /** Umbral de stock bajo 0–1000 (5 por defecto); lista de stock bajo 0–50 (8 por defecto). */
    ProductStatsDto stats(Integer lowStockThreshold, Integer lowStockLimit);
}

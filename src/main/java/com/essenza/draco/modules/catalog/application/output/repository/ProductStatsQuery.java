package com.essenza.draco.modules.catalog.application.output.repository;

import com.essenza.draco.modules.catalog.application.dto.lookup.ProductStatsDto;

/** Agregados del catálogo (solo lectura). */
public interface ProductStatsQuery {

    ProductStatsDto stats(int lowStockThreshold, int lowStockLimit);
}

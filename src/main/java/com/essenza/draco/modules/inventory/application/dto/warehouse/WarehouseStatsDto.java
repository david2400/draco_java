package com.essenza.draco.modules.inventory.application.dto.warehouse;

import java.math.BigDecimal;

/** Contadores del listado de bodegas. */
public record WarehouseStatsDto(long total, long active, long inactive, long unitsOnHand, BigDecimal stockValue,
                                long belowMinCount, Long mainId, String mainName) {
}

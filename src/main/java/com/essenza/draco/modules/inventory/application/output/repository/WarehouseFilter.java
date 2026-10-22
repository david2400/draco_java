package com.essenza.draco.modules.inventory.application.output.repository;

/** Filtros del listado de bodegas. */
public record WarehouseFilter(String q, String type, Boolean active, Long cityId) {
}

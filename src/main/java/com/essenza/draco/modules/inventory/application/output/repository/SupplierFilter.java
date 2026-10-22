package com.essenza.draco.modules.inventory.application.output.repository;

/** Filtros del listado de proveedores. */
public record SupplierFilter(String q, String status, Long cityId) {
}

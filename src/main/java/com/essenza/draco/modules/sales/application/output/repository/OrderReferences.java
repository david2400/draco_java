package com.essenza.draco.modules.sales.application.output.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/** Datos de otros módulos que la orden solo referencia por id (consultas nativas de lectura). */
public interface OrderReferences {

    /** Tienda activa: su bodega (vacío si la tienda no existe o está inactiva). */
    Optional<StoreRef> store(Long storeId);

    boolean warehouseActive(Long warehouseId);

    boolean carrierActive(Long carrierId);

    /** Categoría de cada producto (para los cupones por categoría). */
    Map<Long, Long> categories(Collection<Long> productIds);

    record StoreRef(Long id, String name, Long warehouseId) {
    }
}

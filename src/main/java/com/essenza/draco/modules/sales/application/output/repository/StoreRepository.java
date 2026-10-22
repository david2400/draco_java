package com.essenza.draco.modules.sales.application.output.repository;

import java.util.List;
import java.util.Optional;

import com.essenza.draco.modules.sales.domain.model.cash.Store;

/** Puntos de venta (tiendas). */
public interface StoreRepository {

    List<Store> findAll();

    Optional<Store> findById(Long id);

    Store save(Store store);

    void delete(Long id);

    boolean existsCode(String code, Long excludeId);

    /** Cajas de la tienda (activas o no). */
    long countRegisters(Long storeId);

    /** Cajas de la tienda con turno abierto. */
    long countOpenRegisters(Long storeId);

    /** La bodega existe (no borrada). */
    boolean warehouseExists(Long warehouseId);
}

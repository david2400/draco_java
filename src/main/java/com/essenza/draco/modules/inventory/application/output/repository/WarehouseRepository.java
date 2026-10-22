package com.essenza.draco.modules.inventory.application.output.repository;

import com.essenza.draco.modules.inventory.domain.model.warehouse.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/** Persistencia de la bodega. */
public interface WarehouseRepository {

    Optional<Warehouse> findById(Long id);

    Warehouse save(Warehouse warehouse);

    void delete(Long id);

    Page<Warehouse> search(WarehouseFilter filter, Pageable pageable);

    boolean existsCode(String code, Long excludeId);

    /** Siguiente número para el código automático BOD_<n>. */
    long nextNumber();

    /** Quita la marca de principal a todas menos a {@code exceptId}. */
    void clearMain(Long exceptId);

    Optional<Warehouse> findMain();
}

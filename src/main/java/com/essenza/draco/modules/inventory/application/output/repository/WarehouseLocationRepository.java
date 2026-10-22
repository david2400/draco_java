package com.essenza.draco.modules.inventory.application.output.repository;

import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseLocation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WarehouseLocationRepository {

    List<WarehouseLocation> list(Long warehouseId);

    Optional<WarehouseLocation> findById(Long id);

    WarehouseLocation save(WarehouseLocation location);

    void delete(Long id);

    boolean existsCode(Long warehouseId, String code, Long excludeId);

    Map<Long, Long> countByWarehouse(Collection<Long> warehouseIds);
}

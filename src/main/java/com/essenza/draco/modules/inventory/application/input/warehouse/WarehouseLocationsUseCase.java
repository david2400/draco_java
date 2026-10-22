package com.essenza.draco.modules.inventory.application.input.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.SaveLocationDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseLocationDto;

import java.util.List;

public interface WarehouseLocationsUseCase {
    List<WarehouseLocationDto> list(Long warehouseId);

    /** @param id null = alta */
    WarehouseLocationDto save(Long warehouseId, Long id, SaveLocationDto input);

    void delete(Long warehouseId, Long id);
}

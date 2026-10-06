package com.essenza.draco.modules.inventory.application.input.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.UpdateWarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;

public interface UpdateWarehouseUseCase {
    WarehouseDto update(Long id, UpdateWarehouseDto input);
}

package com.essenza.draco.modules.inventory.application.input.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;

public interface SetMainWarehouseUseCase {
    WarehouseDto setMain(Long id);
}

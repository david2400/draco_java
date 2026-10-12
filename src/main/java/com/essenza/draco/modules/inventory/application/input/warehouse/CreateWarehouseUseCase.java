package com.essenza.draco.modules.inventory.application.input.warehouse;

import com.essenza.draco.modules.inventory.domain.dto.warehouse.CreateWarehouseDto;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.WarehouseDto;

public interface CreateWarehouseUseCase {
    WarehouseDto create(CreateWarehouseDto input);
}

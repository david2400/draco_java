package com.essenza.draco.modules.inventory.application.input.warehouse;

import com.essenza.draco.modules.inventory.domain.dto.warehouse.WarehouseDto;

import java.util.Optional;

public interface FindWarehouseByIdUseCase {
    Optional<WarehouseDto> findById(Long id);
}

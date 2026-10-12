package com.essenza.draco.modules.inventory.application.input.warehouse;

import com.essenza.draco.modules.inventory.domain.dto.warehouse.WarehouseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindWarehousesUseCase {
    Page<WarehouseDto> findAll(Pageable pageable);
}

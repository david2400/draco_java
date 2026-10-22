package com.essenza.draco.modules.inventory.application.input.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseStatsDto;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindWarehousesUseCase {
    WarehouseDto findById(Long id);

    Page<WarehouseDto> search(WarehouseFilter filter, Pageable pageable);

    WarehouseStatsDto stats();
}

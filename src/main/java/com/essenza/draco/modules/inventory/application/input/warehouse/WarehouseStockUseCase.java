package com.essenza.draco.modules.inventory.application.input.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.UpdateMinThresholdDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseLinksDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseMovementDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseStockDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseStockUseCase {
    Page<WarehouseStockDto> stock(Long warehouseId, String q, boolean belowMinOnly, Pageable pageable);

    WarehouseStockDto updateMin(Long warehouseId, Long skuId, UpdateMinThresholdDto input);

    List<WarehouseMovementDto> movements(Long warehouseId, int limit);

    WarehouseLinksDto links(Long warehouseId);
}

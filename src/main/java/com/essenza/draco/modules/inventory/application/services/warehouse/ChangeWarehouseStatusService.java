package com.essenza.draco.modules.inventory.application.services.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.application.input.warehouse.ChangeWarehouseStatusUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseRepository;
import com.essenza.draco.modules.inventory.domain.model.warehouse.Warehouse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChangeWarehouseStatusService implements ChangeWarehouseStatusUseCase {

    private final WarehouseRepository warehouses;
    private final WarehouseSoporte soporte;

    public ChangeWarehouseStatusService(WarehouseRepository warehouses, WarehouseSoporte soporte) {
        this.warehouses = warehouses;
        this.soporte = soporte;
    }

    @Override
    public WarehouseDto setActive(Long id, boolean active) {
        Warehouse current = soporte.warehouse(id);
        if (current.active() == active) {
            return soporte.toDto(current);
        }
        if (!active) {
            soporte.checkCanDeactivate(current);
        }
        return soporte.toDto(warehouses.save(current.withActive(active)));
    }
}

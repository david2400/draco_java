package com.essenza.draco.modules.inventory.application.services.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.SaveWarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.application.input.warehouse.UpdateWarehouseUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseRepository;
import com.essenza.draco.modules.inventory.domain.model.warehouse.Warehouse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Edición: conserva la marca de principal; desactivar pasa por las mismas reglas que el cambio de estado. */
@Service
@Transactional
public class UpdateWarehouseService implements UpdateWarehouseUseCase {

    private final WarehouseRepository warehouses;
    private final WarehouseSoporte soporte;

    public UpdateWarehouseService(WarehouseRepository warehouses, WarehouseSoporte soporte) {
        this.warehouses = warehouses;
        this.soporte = soporte;
    }

    @Override
    public WarehouseDto update(Long id, SaveWarehouseDto input) {
        Warehouse current = soporte.warehouse(id);
        boolean active = input.active() == null ? current.active() : input.active();
        if (current.active() && !active) {
            soporte.checkCanDeactivate(current);
        }
        Warehouse next = soporte.from(id, input, current.main(), active);
        soporte.checkUnique(next, id);
        return soporte.toDto(warehouses.save(next));
    }
}

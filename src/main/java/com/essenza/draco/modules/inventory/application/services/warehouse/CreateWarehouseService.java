package com.essenza.draco.modules.inventory.application.services.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.SaveWarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.application.input.warehouse.CreateWarehouseUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseRepository;
import com.essenza.draco.modules.inventory.domain.model.warehouse.Warehouse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Alta: código único; si aún no hay bodega principal, la nueva lo es (cuando su tipo lo permite). */
@Service
@Transactional
public class CreateWarehouseService implements CreateWarehouseUseCase {

    private final WarehouseRepository warehouses;
    private final WarehouseSoporte soporte;

    public CreateWarehouseService(WarehouseRepository warehouses, WarehouseSoporte soporte) {
        this.warehouses = warehouses;
        this.soporte = soporte;
    }

    @Override
    public WarehouseDto create(SaveWarehouseDto input) {
        boolean active = input.active() == null || input.active();
        Warehouse draft = soporte.from(null, input, false, active);
        boolean main = active && draft.type().canBeMain() && warehouses.findMain().isEmpty();
        Warehouse warehouse = draft.asMain(main);
        soporte.checkUnique(warehouse, null);
        return soporte.toDto(warehouses.save(warehouse));
    }
}

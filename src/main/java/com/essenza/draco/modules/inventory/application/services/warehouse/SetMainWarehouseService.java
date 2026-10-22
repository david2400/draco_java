package com.essenza.draco.modules.inventory.application.services.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.application.input.warehouse.SetMainWarehouseUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseRepository;
import com.essenza.draco.modules.inventory.domain.model.warehouse.Warehouse;
import com.essenza.draco.shared.exceptions.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Una sola principal: la elegida (activa, de tipo almacén o tienda) y se desmarca la anterior. */
@Service
@Transactional
public class SetMainWarehouseService implements SetMainWarehouseUseCase {

    private final WarehouseRepository warehouses;
    private final WarehouseSoporte soporte;

    public SetMainWarehouseService(WarehouseRepository warehouses, WarehouseSoporte soporte) {
        this.warehouses = warehouses;
        this.soporte = soporte;
    }

    @Override
    public WarehouseDto setMain(Long id) {
        Warehouse current = soporte.warehouse(id);
        if (!current.active()) {
            throw new ConflictException("Activa la bodega " + current.name() + " antes de marcarla como principal.");
        }
        Warehouse main = current.asMain(true);
        warehouses.clearMain(id);
        return soporte.toDto(warehouses.save(main));
    }
}

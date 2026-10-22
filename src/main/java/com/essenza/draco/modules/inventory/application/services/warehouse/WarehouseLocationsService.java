package com.essenza.draco.modules.inventory.application.services.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.SaveLocationDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseLocationDto;
import com.essenza.draco.modules.inventory.application.input.warehouse.WarehouseLocationsUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseLocationRepository;
import com.essenza.draco.modules.inventory.domain.model.warehouse.LocationType;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseLocation;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Catálogo de ubicaciones internas (zona, pasillo, estante, nivel) con código único por bodega. */
@Service
@Transactional
public class WarehouseLocationsService implements WarehouseLocationsUseCase {

    private final WarehouseLocationRepository locations;
    private final WarehouseSoporte soporte;

    public WarehouseLocationsService(WarehouseLocationRepository locations, WarehouseSoporte soporte) {
        this.locations = locations;
        this.soporte = soporte;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseLocationDto> list(Long warehouseId) {
        soporte.warehouse(warehouseId);
        return locations.list(warehouseId).stream().map(WarehouseLocationsService::toDto).toList();
    }

    @Override
    public WarehouseLocationDto save(Long warehouseId, Long id, SaveLocationDto in) {
        soporte.warehouse(warehouseId);
        if (id != null) {
            locations.findById(id).filter(l -> l.warehouseId().equals(warehouseId))
                    .orElseThrow(() -> new NotFoundException("La bodega no tiene la ubicación " + id + "."));
        }
        WarehouseLocation location = new WarehouseLocation(id, warehouseId, in.code(), in.zone(), in.aisle(), in.rack(),
                in.level(), LocationType.of(in.locationType()), in.capacityUnits(), in.active() == null || in.active());
        if (locations.existsCode(warehouseId, location.code(), id)) {
            throw new ConflictException("Ya existe la ubicación " + location.code() + " en esta bodega.");
        }
        return toDto(locations.save(location));
    }

    @Override
    public void delete(Long warehouseId, Long id) {
        locations.findById(id).filter(l -> l.warehouseId().equals(warehouseId))
                .orElseThrow(() -> new NotFoundException("La bodega no tiene la ubicación " + id + "."));
        locations.delete(id);
    }

    private static WarehouseLocationDto toDto(WarehouseLocation l) {
        return new WarehouseLocationDto(l.id(), l.warehouseId(), l.code(), l.zone(), l.aisle(), l.rack(), l.level(),
                l.type().name(), l.capacityUnits(), l.active());
    }
}

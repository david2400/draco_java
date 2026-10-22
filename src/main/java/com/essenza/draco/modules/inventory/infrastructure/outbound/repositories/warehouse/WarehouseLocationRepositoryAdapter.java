package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.warehouse;

import com.essenza.draco.modules.inventory.application.output.repository.WarehouseLocationRepository;
import com.essenza.draco.modules.inventory.domain.model.warehouse.LocationType;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseLocation;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.WarehouseLocationEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class WarehouseLocationRepositoryAdapter implements WarehouseLocationRepository {

    private final JpaWarehouseLocationRepository jpa;

    public WarehouseLocationRepositoryAdapter(JpaWarehouseLocationRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<WarehouseLocation> list(Long warehouseId) {
        return jpa.findByWarehouseIdOrderByCodeAsc(warehouseId).stream().map(WarehouseLocationRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<WarehouseLocation> findById(Long id) {
        return jpa.findById(id).map(WarehouseLocationRepositoryAdapter::toDomain);
    }

    @Override
    public WarehouseLocation save(WarehouseLocation l) {
        WarehouseLocationEntity e = l.id() == null ? new WarehouseLocationEntity()
                : jpa.findById(l.id()).orElseGet(WarehouseLocationEntity::new);
        e.setWarehouseId(l.warehouseId());
        e.setCode(l.code());
        e.setZone(l.zone());
        e.setAisle(l.aisle());
        e.setRack(l.rack());
        e.setLevel(l.level());
        e.setLocationType(l.type().name());
        e.setCapacityUnits(l.capacityUnits());
        e.setActive(l.active());
        return toDomain(jpa.saveAndFlush(e));
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsCode(Long warehouseId, String code, Long excludeId) {
        return excludeId == null ? jpa.existsByWarehouseIdAndCode(warehouseId, code)
                : jpa.existsByWarehouseIdAndCodeAndIdNot(warehouseId, code, excludeId);
    }

    @Override
    public Map<Long, Long> countByWarehouse(Collection<Long> warehouseIds) {
        if (warehouseIds == null || warehouseIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Long> out = new HashMap<>();
        jpa.countByWarehouse(warehouseIds).forEach(r -> out.put((Long) r[0], ((Number) r[1]).longValue()));
        return out;
    }

    private static WarehouseLocation toDomain(WarehouseLocationEntity e) {
        return new WarehouseLocation(e.getId(), e.getWarehouseId(), e.getCode(), e.getZone(), e.getAisle(), e.getRack(),
                e.getLevel(), LocationType.of(e.getLocationType()), e.getCapacityUnits(), !Boolean.FALSE.equals(e.getActive()));
    }
}

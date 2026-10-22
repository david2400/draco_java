package com.essenza.draco.modules.inventory.application.services.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.SaveWarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseStockDto;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseInsights;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseLocationRepository;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseRepository;
import com.essenza.draco.modules.inventory.domain.model.warehouse.Warehouse;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseCapacity;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseOperation;
import com.essenza.draco.modules.inventory.domain.model.warehouse.WarehouseType;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Lógica compartida por los casos de uso de bodegas: carga, armado desde el formulario, reglas de baja y DTO. */
@Component
class WarehouseSoporte {

    private final WarehouseRepository warehouses;
    private final WarehouseInsights insights;
    private final WarehouseLocationRepository locations;

    WarehouseSoporte(WarehouseRepository warehouses, WarehouseInsights insights, WarehouseLocationRepository locations) {
        this.warehouses = warehouses;
        this.insights = insights;
        this.locations = locations;
    }

    Warehouse warehouse(Long id) {
        return warehouses.findById(id).orElseThrow(() -> new NotFoundException("Bodega no encontrada: " + id));
    }

    Warehouse from(Long id, SaveWarehouseDto in, boolean main, boolean active) {
        String code = in.code() == null || in.code().isBlank() ? "BOD_" + warehouses.nextNumber() : in.code();
        WarehouseType type = WarehouseType.of(in.warehouseType());
        // En tránsito no se vende: si no llega el dato se apaga en lugar de rechazar.
        boolean sales = in.allowSales() == null ? type != WarehouseType.TRANSITO : in.allowSales();
        WarehouseOperation operation = new WarehouseOperation(sales,
                in.allowOnline() == null || in.allowOnline(), in.allowReceiving() == null || in.allowReceiving(),
                in.fulfillmentPriority() == null ? 10 : in.fulfillmentPriority());
        return new Warehouse(id, code, in.name(), type, main, active, operation, in.address(), in.countryId(),
                in.stateId(), in.cityId(), in.city(), in.managerName(), in.managerPhone(), in.managerEmail(),
                in.openingHours(), in.notes(), new WarehouseCapacity(in.areaM2(), in.capacityUnits(), in.capacityPositions()));
    }

    void checkUnique(Warehouse warehouse, Long excludeId) {
        if (warehouses.existsCode(warehouse.code(), excludeId)) {
            throw new ConflictException("Ya existe una bodega con el código \"" + warehouse.code() + "\".");
        }
    }

    /** Desactivar: no con reservas pendientes ni si una tienda activa vende desde ella (la principal la frena el dominio). */
    void checkCanDeactivate(Warehouse w) {
        if (w.main()) {
            throw new ConflictException("La bodega principal no se puede desactivar: marca otra como principal primero.");
        }
        WarehouseInsights.StockSummary s = insights.summaries(List.of(w.id())).getOrDefault(w.id(), WarehouseInsights.StockSummary.EMPTY);
        if (s.reserved() > 0) {
            throw new ConflictException("La bodega " + w.name() + " tiene " + s.reserved()
                    + " unidad(es) reservadas para pedidos: despáchalos o libéralos antes de desactivarla.");
        }
        long stores = insights.activeStores(w.id());
        if (stores > 0) {
            throw new ConflictException("La bodega " + w.name() + " surte " + stores
                    + " tienda(s) activa(s): asígnales otra bodega antes de desactivarla.");
        }
    }

    WarehouseDto toDto(Warehouse w) {
        return toDtos(List.of(w)).get(0);
    }

    List<WarehouseDto> toDtos(List<Warehouse> list) {
        List<Long> ids = list.stream().map(Warehouse::id).toList();
        Map<Long, WarehouseInsights.StockSummary> summaries = insights.summaries(ids);
        Map<Long, Long> locationCounts = locations.countByWarehouse(ids);
        Map<Long, Long> storeCounts = insights.activeStoreCounts(ids);
        return list.stream().map(w -> {
            WarehouseInsights.StockSummary s = summaries.getOrDefault(w.id(), WarehouseInsights.StockSummary.EMPTY);
            WarehouseOperation op = w.operation();
            WarehouseCapacity cap = w.capacity();
            return new WarehouseDto(w.id(), w.code(), w.name(), w.type().name(), w.main(), w.active(), op.sales(),
                    op.online(), op.receiving(), op.priority(), w.address(), w.countryId(), w.stateId(), w.cityId(),
                    w.city(), w.managerName(), w.managerPhone(), w.managerEmail(), w.openingHours(), w.notes(),
                    cap.areaM2(), cap.units(), cap.positions(), s.skus(), s.onHand(), s.reserved(),
                    Math.max(0, s.onHand() - s.reserved()), s.value(), s.belowMin(), cap.occupancy(s.onHand()),
                    locationCounts.getOrDefault(w.id(), 0L), storeCounts.getOrDefault(w.id(), 0L));
        }).toList();
    }

    static WarehouseStockDto stockDto(WarehouseInsights.StockLine l) {
        BigDecimal cost = l.unitCost();
        BigDecimal value = cost == null ? null : cost.multiply(BigDecimal.valueOf(l.onHand()));
        int available = Math.max(0, l.onHand() - l.reserved());
        return new WarehouseStockDto(l.skuId(), l.productId(), l.skuCode(), l.productName(), l.skuName(), l.onHand(),
                l.reserved(), available, l.minThreshold(), cost, value, l.minThreshold() > 0 && available <= l.minThreshold());
    }
}

package com.essenza.draco.modules.inventory.application.services.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseStatsDto;
import com.essenza.draco.modules.inventory.application.input.warehouse.FindWarehousesUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseFilter;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseInsights;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseRepository;
import com.essenza.draco.modules.inventory.domain.model.warehouse.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta de bodegas con su resumen de existencias (una consulta agregada por página). */
@Service
@Transactional(readOnly = true)
public class FindWarehousesService implements FindWarehousesUseCase {

    private final WarehouseRepository warehouses;
    private final WarehouseInsights insights;
    private final WarehouseSoporte soporte;

    public FindWarehousesService(WarehouseRepository warehouses, WarehouseInsights insights, WarehouseSoporte soporte) {
        this.warehouses = warehouses;
        this.insights = insights;
        this.soporte = soporte;
    }

    @Override
    public WarehouseDto findById(Long id) {
        return soporte.toDto(soporte.warehouse(id));
    }

    @Override
    public Page<WarehouseDto> search(WarehouseFilter filter, Pageable pageable) {
        Page<Warehouse> page = warehouses.search(filter, pageable);
        return new PageImpl<>(soporte.toDtos(page.getContent()), pageable, page.getTotalElements());
    }

    @Override
    public WarehouseStatsDto stats() {
        Page<Warehouse> all = warehouses.search(new WarehouseFilter(null, null, null, null), PageRequest.of(0, 1000));
        long active = all.getContent().stream().filter(Warehouse::active).count();
        WarehouseInsights.StockSummary totals = insights.totals();
        Warehouse main = warehouses.findMain().orElse(null);
        return new WarehouseStatsDto(all.getTotalElements(), active, all.getTotalElements() - active, totals.onHand(),
                totals.value(), totals.belowMin(), main == null ? null : main.id(), main == null ? null : main.name());
    }
}

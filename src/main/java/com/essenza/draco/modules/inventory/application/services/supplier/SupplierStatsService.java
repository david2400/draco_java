package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierStatsDto;
import com.essenza.draco.modules.inventory.application.input.supplier.SupplierStatsUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SupplierStatsService implements SupplierStatsUseCase {

    private final SupplierRepository suppliers;

    public SupplierStatsService(SupplierRepository suppliers) {
        this.suppliers = suppliers;
    }

    @Override
    public SupplierStatsDto stats() {
        SupplierRepository.Counts c = suppliers.counts();
        return new SupplierStatsDto(c.total(), c.active(), c.blocked(), c.inactive(), c.withoutDocument(), c.credit());
    }
}

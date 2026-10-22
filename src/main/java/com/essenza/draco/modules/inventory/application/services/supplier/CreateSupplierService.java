package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SaveSupplierDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.application.input.supplier.CreateSupplierUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierRepository;
import com.essenza.draco.modules.inventory.domain.model.supplier.Supplier;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Alta: documento obligatorio, código y documento únicos. */
@Service
@Transactional
public class CreateSupplierService implements CreateSupplierUseCase {

    private final SupplierRepository suppliers;
    private final SupplierSoporte soporte;

    public CreateSupplierService(SupplierRepository suppliers, SupplierSoporte soporte) {
        this.suppliers = suppliers;
        this.soporte = soporte;
    }

    @Override
    public SupplierDto create(SaveSupplierDto input) {
        Supplier supplier = soporte.from(null, input, SupplierStatus.of(input.status()), input.blockReason());
        soporte.checkUnique(supplier, null);
        return soporte.detail(suppliers.save(supplier));
    }
}

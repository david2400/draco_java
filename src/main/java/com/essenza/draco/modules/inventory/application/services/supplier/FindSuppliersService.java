package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.application.input.supplier.FindSuppliersUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FindSuppliersService implements FindSuppliersUseCase {

    private final SupplierRepository suppliers;
    private final SupplierSoporte soporte;

    public FindSuppliersService(SupplierRepository suppliers, SupplierSoporte soporte) {
        this.suppliers = suppliers;
        this.soporte = soporte;
    }

    @Override
    public List<SupplierDto> findAll() {
        return soporte.summaries(suppliers.findAll());
    }
}

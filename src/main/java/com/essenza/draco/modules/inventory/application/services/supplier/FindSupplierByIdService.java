package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.application.input.supplier.FindSupplierByIdUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FindSupplierByIdService implements FindSupplierByIdUseCase {

    private final SupplierSoporte soporte;

    public FindSupplierByIdService(SupplierSoporte soporte) {
        this.soporte = soporte;
    }

    @Override
    public SupplierDto findById(Long id) {
        return soporte.detail(soporte.supplier(id));
    }
}

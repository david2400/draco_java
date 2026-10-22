package com.essenza.draco.modules.inventory.application.input.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;

public interface FindSupplierByIdUseCase {
    SupplierDto findById(Long id);
}

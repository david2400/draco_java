package com.essenza.draco.modules.inventory.application.input.supplier;

import com.essenza.draco.modules.inventory.domain.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.UpdateSupplierDto;

public interface UpdateSupplierUseCase {
    SupplierDto update(Long id, UpdateSupplierDto input);
}

package com.essenza.draco.modules.inventory.application.input.supplier;

import com.essenza.draco.modules.inventory.domain.dto.supplier.CreateSupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.SupplierDto;

public interface CreateSupplierUseCase {
    SupplierDto create(CreateSupplierDto input);
}

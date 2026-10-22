package com.essenza.draco.modules.inventory.application.input.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SaveSupplierDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;

public interface CreateSupplierUseCase {
    SupplierDto create(SaveSupplierDto input);
}

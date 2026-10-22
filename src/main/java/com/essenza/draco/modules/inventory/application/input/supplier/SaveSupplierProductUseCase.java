package com.essenza.draco.modules.inventory.application.input.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SaveSupplierProductDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierProductDto;

public interface SaveSupplierProductUseCase {
    /** @param id null = alta */
    SupplierProductDto save(Long supplierId, Long id, SaveSupplierProductDto input);
}

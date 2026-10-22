package com.essenza.draco.modules.inventory.application.input.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.ChangeSupplierStatusDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;

public interface ChangeSupplierStatusUseCase {
    SupplierDto change(Long id, ChangeSupplierStatusDto input);
}

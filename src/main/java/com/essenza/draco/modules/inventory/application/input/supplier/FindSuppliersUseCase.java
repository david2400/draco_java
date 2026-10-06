package com.essenza.draco.modules.inventory.application.input.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;

import java.util.List;

public interface FindSuppliersUseCase {
    List<SupplierDto> findAll();
}

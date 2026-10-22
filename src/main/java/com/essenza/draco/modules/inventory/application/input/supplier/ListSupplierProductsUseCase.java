package com.essenza.draco.modules.inventory.application.input.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierProductDto;

import java.util.List;

public interface ListSupplierProductsUseCase {
    List<SupplierProductDto> list(Long supplierId);
}

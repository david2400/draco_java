package com.essenza.draco.modules.inventory.application.input.supplier;

public interface DeleteSupplierProductUseCase {
    void delete(Long supplierId, Long id);
}

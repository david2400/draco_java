package com.essenza.draco.modules.inventory.application.input.supplier;

public interface DeleteSupplierUseCase {
    /** 404 si no existe; 409 si productos del catálogo lo tienen como proveedor principal. */
    void delete(Long id);
}

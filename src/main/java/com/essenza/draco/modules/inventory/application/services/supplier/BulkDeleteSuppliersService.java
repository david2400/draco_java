package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.input.supplier.BulkDeleteSuppliersUseCase;
import com.essenza.draco.modules.inventory.application.input.supplier.DeleteSupplierUseCase;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/** Cada proveedor se borra en su propia transacción: un fallo no revierte los demás. */
@Service
public class BulkDeleteSuppliersService implements BulkDeleteSuppliersUseCase {

    private final DeleteSupplierUseCase deleteSupplier;

    public BulkDeleteSuppliersService(DeleteSupplierUseCase deleteSupplier) {
        this.deleteSupplier = deleteSupplier;
    }

    @Override
    public BulkOperationResult deleteAll(List<Long> ids) {
        List<Long> unique = ids.stream().distinct().toList();
        BulkOperationResult.Builder result = new BulkOperationResult.Builder(unique.size());
        for (Long id : unique) {
            try {
                deleteSupplier.delete(id);
                result.success();
            } catch (NotFoundException | ConflictException ex) {
                result.failure(id, ex.getMessage());
            }
        }
        return result.build();
    }
}

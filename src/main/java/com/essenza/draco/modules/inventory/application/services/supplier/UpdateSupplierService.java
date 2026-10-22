package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SaveSupplierDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.application.input.supplier.UpdateSupplierUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierProductRepository;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierRepository;
import com.essenza.draco.modules.inventory.domain.model.supplier.Supplier;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Edición de la ficha; sin estado en el cuerpo se conserva el actual (y su motivo de bloqueo). */
@Service
@Transactional
public class UpdateSupplierService implements UpdateSupplierUseCase {

    private final SupplierRepository suppliers;
    private final SupplierProductRepository products;
    private final SupplierSoporte soporte;

    public UpdateSupplierService(SupplierRepository suppliers, SupplierProductRepository products, SupplierSoporte soporte) {
        this.suppliers = suppliers;
        this.products = products;
        this.soporte = soporte;
    }

    @Override
    public SupplierDto update(Long id, SaveSupplierDto input) {
        Supplier current = soporte.supplier(id);
        boolean keepStatus = input.status() == null || input.status().isBlank();
        SupplierStatus status = keepStatus ? current.status() : SupplierStatus.of(input.status());
        String reason = keepStatus && input.blockReason() == null ? current.blockReason() : input.blockReason();
        Supplier next = soporte.from(id, input, status, reason);
        soporte.checkUnique(next, id);
        Supplier saved = suppliers.save(next);
        if (saved.status() != SupplierStatus.ACTIVE) {
            products.clearPreferredOfSupplier(saved.id());
        }
        return soporte.detail(saved);
    }
}

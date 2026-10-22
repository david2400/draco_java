package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.ChangeSupplierStatusDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.application.input.supplier.ChangeSupplierStatusUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierProductRepository;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierRepository;
import com.essenza.draco.modules.inventory.domain.model.supplier.Supplier;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Activar, bloquear (con motivo) o desactivar sin editar la ficha. */
@Service
@Transactional
public class ChangeSupplierStatusService implements ChangeSupplierStatusUseCase {

    private final SupplierRepository suppliers;
    private final SupplierProductRepository products;
    private final SupplierSoporte soporte;

    public ChangeSupplierStatusService(SupplierRepository suppliers, SupplierProductRepository products, SupplierSoporte soporte) {
        this.suppliers = suppliers;
        this.products = products;
        this.soporte = soporte;
    }

    @Override
    public SupplierDto change(Long id, ChangeSupplierStatusDto input) {
        Supplier current = soporte.supplier(id);
        Supplier next = current.withStatus(SupplierStatus.of(input.status()), input.reason());
        Supplier saved = suppliers.save(next);
        if (saved.status() != SupplierStatus.ACTIVE) {
            products.clearPreferredOfSupplier(saved.id());
        }
        return soporte.detail(saved);
    }
}

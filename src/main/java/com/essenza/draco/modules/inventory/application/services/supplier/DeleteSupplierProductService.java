package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.input.supplier.DeleteSupplierProductUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierProductRepository;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteSupplierProductService implements DeleteSupplierProductUseCase {

    private final SupplierProductRepository products;

    public DeleteSupplierProductService(SupplierProductRepository products) {
        this.products = products;
    }

    @Override
    public void delete(Long supplierId, Long id) {
        products.findById(id).filter(p -> p.supplierId().equals(supplierId))
                .orElseThrow(() -> new NotFoundException("El proveedor no surte el producto " + id + "."));
        products.delete(id);
    }
}

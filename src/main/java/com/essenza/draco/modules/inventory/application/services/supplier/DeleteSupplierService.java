package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.input.supplier.DeleteSupplierUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierProductRepository;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierRepository;
import com.essenza.draco.modules.inventory.domain.model.supplier.Supplier;
import com.essenza.draco.shared.exceptions.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Borrado lógico. No se borra si productos del catálogo lo tienen como proveedor principal
 * (se sugiere desactivarlo); los SKU que surtía se quitan con él.
 */
@Service
@Transactional
public class DeleteSupplierService implements DeleteSupplierUseCase {

    private final SupplierRepository suppliers;
    private final SupplierProductRepository products;
    private final SupplierSoporte soporte;

    public DeleteSupplierService(SupplierRepository suppliers, SupplierProductRepository products, SupplierSoporte soporte) {
        this.suppliers = suppliers;
        this.products = products;
        this.soporte = soporte;
    }

    @Override
    public void delete(Long id) {
        Supplier supplier = soporte.supplier(id);
        long catalog = suppliers.countCatalogProducts(id);
        if (catalog > 0) {
            throw new ConflictException("El proveedor " + supplier.displayName() + " es el proveedor principal de " + catalog
                    + " producto(s) del catálogo: cámbialos o desactívalo en lugar de eliminarlo.");
        }
        products.deleteBySupplier(id);
        suppliers.delete(id);
    }
}

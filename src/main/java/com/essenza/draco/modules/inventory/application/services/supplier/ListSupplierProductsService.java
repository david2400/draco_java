package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierProductDto;
import com.essenza.draco.modules.inventory.application.input.supplier.ListSupplierProductsUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierProductRepository;
import com.essenza.draco.modules.inventory.domain.model.supplier.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListSupplierProductsService implements ListSupplierProductsUseCase {

    private final SupplierProductRepository products;
    private final SupplierSoporte soporte;

    public ListSupplierProductsService(SupplierProductRepository products, SupplierSoporte soporte) {
        this.products = products;
        this.soporte = soporte;
    }

    @Override
    public List<SupplierProductDto> list(Long supplierId) {
        Supplier supplier = soporte.supplier(supplierId);
        return products.listBySupplier(supplierId).stream().map(view -> soporte.productDto(view, supplier)).toList();
    }
}

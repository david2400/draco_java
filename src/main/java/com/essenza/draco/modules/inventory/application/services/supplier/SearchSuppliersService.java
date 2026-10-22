package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.application.input.supplier.SearchSuppliersUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierFilter;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierRepository;
import com.essenza.draco.modules.inventory.domain.model.supplier.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Listado paginado; los SKU que surte cada proveedor se cuentan en una sola consulta. */
@Service
@Transactional(readOnly = true)
public class SearchSuppliersService implements SearchSuppliersUseCase {

    private final SupplierRepository suppliers;
    private final SupplierSoporte soporte;

    public SearchSuppliersService(SupplierRepository suppliers, SupplierSoporte soporte) {
        this.suppliers = suppliers;
        this.soporte = soporte;
    }

    @Override
    public Page<SupplierDto> search(SupplierFilter filter, Pageable pageable) {
        Page<Supplier> page = suppliers.search(filter, pageable);
        return new PageImpl<>(soporte.summaries(page.getContent()), pageable, page.getTotalElements());
    }
}

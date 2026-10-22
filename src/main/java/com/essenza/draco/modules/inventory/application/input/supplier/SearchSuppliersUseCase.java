package com.essenza.draco.modules.inventory.application.input.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchSuppliersUseCase {
    Page<SupplierDto> search(SupplierFilter filter, Pageable pageable);
}

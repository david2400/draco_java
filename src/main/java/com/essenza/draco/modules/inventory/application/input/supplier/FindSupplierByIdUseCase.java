package com.essenza.draco.modules.inventory.application.input.supplier;

import com.essenza.draco.modules.inventory.domain.dto.supplier.SupplierDto;

import java.util.Optional;

public interface FindSupplierByIdUseCase {
    Optional<SupplierDto> findById(Long id);
}

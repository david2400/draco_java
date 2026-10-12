package com.essenza.draco.modules.inventory.application.output.repository;

import com.essenza.draco.modules.inventory.domain.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.CreateSupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.UpdateSupplierDto;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository {

    SupplierDto create(CreateSupplierDto input);

    SupplierDto update(Long id, UpdateSupplierDto input);

    boolean deleteById(Long id);

    Optional<SupplierDto> findById(Long id);

    List<SupplierDto> findAll();
}

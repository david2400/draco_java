package com.essenza.draco.modules.inventory.application.services;

import com.essenza.draco.modules.inventory.application.input.supplier.*;
import com.essenza.draco.modules.inventory.domain.dto.supplier.CreateSupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.UpdateSupplierDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.supplier.SupplierRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

//Proveedor
@Service
@Transactional
public class SupplierServiceImpl implements CreateSupplierUseCase, UpdateSupplierUseCase, DeleteSupplierUseCase, FindSuppliersUseCase, FindSupplierByIdUseCase {

    private final SupplierRepositoryAdapter supplierRepository;

    public SupplierServiceImpl(SupplierRepositoryAdapter supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public SupplierDto create(CreateSupplierDto input) {
        return supplierRepository.create(input);
    }

    @Override
    public SupplierDto update(Long id, UpdateSupplierDto input) {
        return supplierRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return supplierRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierDto> findById(Long id) {
        return supplierRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDto> findAll() {
        return supplierRepository.findAll();
    }
}

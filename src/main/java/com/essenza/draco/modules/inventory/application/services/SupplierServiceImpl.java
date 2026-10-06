package com.essenza.draco.modules.inventory.application.services;

import com.essenza.draco.modules.inventory.application.input.supplier.*;
import com.essenza.draco.modules.inventory.application.dto.supplier.CreateSupplierDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.UpdateSupplierDto;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

//Proveedor
@Service
@Transactional
public class SupplierServiceImpl implements CreateSupplierUseCase, UpdateSupplierUseCase, DeleteSupplierUseCase, FindSuppliersUseCase, FindSupplierByIdUseCase {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
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

package com.essenza.draco.modules.inventory.application.services;

import com.essenza.draco.modules.inventory.application.input.warehouse.*;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.CreateWarehouseDto;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.UpdateWarehouseDto;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.warehouse.WarehouseRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//Bodega
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements CreateWarehouseUseCase,
        UpdateWarehouseUseCase,
        DeleteWarehouseUseCase,
        FindWarehouseByIdUseCase,
        FindWarehousesUseCase
{

    private final WarehouseRepositoryAdapter repository;

    public WarehouseDto create(CreateWarehouseDto dto) {
        repository.findByCode(dto.getCode()).ifPresent(w -> {
            throw new IllegalArgumentException("Warehouse code already exists: " + dto.getCode());
        });
        return repository.create(dto);
    }

    public Page<WarehouseDto> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public WarehouseDto update(Long id, UpdateWarehouseDto dto) {
        dto.setId(id);
        var current = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + id));
        if (dto.getCode() != null && !dto.getCode().equals(current.getCode())) {
            repository.findByCode(dto.getCode()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Warehouse code already exists: " + dto.getCode());
                }
            });
        }
        return repository.update(id, dto);
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<WarehouseDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<WarehouseDto> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}

package com.essenza.draco.modules.inventory.application.services;

import com.essenza.draco.modules.inventory.application.input.warehouse.*;
import com.essenza.draco.modules.inventory.application.dto.warehouse.CreateWarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.UpdateWarehouseDto;
import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.application.output.repository.StockItemRepository;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseRepository;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import java.util.List;
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

    private final WarehouseRepository repository;
    private final StockItemRepository stockRepository;

    public WarehouseDto create(CreateWarehouseDto dto) {
        repository.findByCode(dto.getCode()).ifPresent(w -> {
            throw new ConflictException("Ya existe una bodega con el código \"" + dto.getCode() + "\".");
        });
        return repository.create(dto);
    }

    public Page<WarehouseDto> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public WarehouseDto update(Long id, UpdateWarehouseDto dto) {
        dto.setId(id);
        var current = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bodega no encontrada: " + id));
        if (dto.getCode() != null && !dto.getCode().equals(current.getCode())) {
            repository.findByCode(dto.getCode()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ConflictException("Ya existe una bodega con el código \"" + dto.getCode() + "\".");
                }
            });
        }
        return repository.update(id, dto);
    }

    @Override
    public boolean deleteById(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Bodega no encontrada: " + id);
        }
        long units = stockRepository.totalOnHandInWarehouse(id);
        if (units > 0) {
            throw new ConflictException("No se puede eliminar la bodega: tiene " + units
                    + " unidad(es) en stock. Transfiérelas a otra bodega primero.");
        }
        return repository.deleteById(id);
    }

    /** Elimina varias bodegas; cada una se valida por separado. */
    public BulkOperationResult deleteAll(List<Long> ids) {
        List<Long> unique = ids.stream().distinct().toList();
        BulkOperationResult.Builder result = new BulkOperationResult.Builder(unique.size());
        for (Long id : unique) {
            try {
                deleteById(id);
                result.success();
            } catch (NotFoundException | ConflictException ex) {
                result.failure(id, ex.getMessage());
            }
        }
        return result.build();
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

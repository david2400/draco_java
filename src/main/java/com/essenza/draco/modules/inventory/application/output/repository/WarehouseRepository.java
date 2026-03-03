package com.essenza.draco.modules.inventory.application.output.repository;

import com.essenza.draco.modules.inventory.domain.dto.warehouse.CreateWarehouseDto;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.UpdateWarehouseDto;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.WarehouseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface WarehouseRepository {

    WarehouseDto create(CreateWarehouseDto input);

    WarehouseDto update(Long id, UpdateWarehouseDto input);

    boolean deleteById(Long id);

    Optional<WarehouseDto> findById(Long id);

    Page<WarehouseDto> findAll(Pageable pageable);

    Optional<WarehouseDto> findByCode(String code);
}

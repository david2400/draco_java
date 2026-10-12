package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.warehouse;

import com.essenza.draco.modules.inventory.application.output.repository.WarehouseRepository;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.CreateWarehouseDto;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.UpdateWarehouseDto;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.mappers.WarehouseMapper;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.WarehouseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WarehouseRepositoryAdapter implements WarehouseRepository {

    private final JpaWarehouseRepository jpa;
    private final WarehouseMapper mapper;

    @Override
    @Transactional
    public WarehouseDto create(CreateWarehouseDto input) {
        WarehouseEntity entity = mapper.toEntity(input);
        WarehouseEntity saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public WarehouseDto update(Long id, UpdateWarehouseDto input) {
        WarehouseEntity entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + id));
        mapper.updateEntity(input, entity);
        WarehouseEntity saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }

    @Override
    public Optional<WarehouseDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public Page<WarehouseDto> findAll(Pageable pageable) {
        return jpa.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Optional<WarehouseDto> findByCode(String code) {
        return jpa.findByCode(code).map(mapper::toDto);
    }
}

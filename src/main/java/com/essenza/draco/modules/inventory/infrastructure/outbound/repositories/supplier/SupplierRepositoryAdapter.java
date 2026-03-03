package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.supplier;

import com.essenza.draco.modules.inventory.application.output.repository.SupplierRepository;
import com.essenza.draco.modules.inventory.infrastructure.outbound.mappers.SupplierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.inventory.domain.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.CreateSupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.UpdateSupplierDto;

import java.util.List;
import java.util.Optional;

@Repository
public class SupplierRepositoryAdapter implements SupplierRepository {

    private final JpaSupplierRepository jpa;
    private final SupplierMapper mapper;

    public SupplierRepositoryAdapter(JpaSupplierRepository jpa, SupplierMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public SupplierDto create(CreateSupplierDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public SupplierDto update(Long id, UpdateSupplierDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + id));
        mapper.updateEntityFromDto(input, entity);
        var updated = jpa.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }

    @Override
    public Optional<SupplierDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<SupplierDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}

package com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.repositories.dispatch_product;

import com.essenza.draco.modules.shipping_logistics.dispatch.application.output.repository.DispatchProductRepository;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.CreateDispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.DispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.UpdateDispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.mappers.DispatchProductMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DispatchProductRepositoryAdapter implements DispatchProductRepository {

    private final JpaDispatchProductRepository jpa;
    private final DispatchProductMapper mapper;

    public DispatchProductRepositoryAdapter(JpaDispatchProductRepository jpa, DispatchProductMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }


    @Override
    public DispatchProductDto create(CreateDispatchProductDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public DispatchProductDto update(Long id, UpdateDispatchProductDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DispatchProduct not found: " + id));
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
    public Optional<DispatchProductDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<DispatchProductDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}

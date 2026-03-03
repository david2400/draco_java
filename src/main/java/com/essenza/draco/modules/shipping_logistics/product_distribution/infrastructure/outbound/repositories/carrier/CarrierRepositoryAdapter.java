package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.repositories.carrier;

import com.essenza.draco.modules.shipping_logistics.product_distribution.application.output.repository.CarrierRepository;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CreateCarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.UpdateCarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.mappers.CarrierMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CarrierRepositoryAdapter implements CarrierRepository {

    private final JpaCarrierRepository jpa;
    private final CarrierMapper mapper;

    public CarrierRepositoryAdapter(JpaCarrierRepository jpa, CarrierMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public CarrierDto create(CreateCarrierDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public CarrierDto update(Long id, UpdateCarrierDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carrier not found: " + id));
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
    public Optional<CarrierDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<CarrierDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}

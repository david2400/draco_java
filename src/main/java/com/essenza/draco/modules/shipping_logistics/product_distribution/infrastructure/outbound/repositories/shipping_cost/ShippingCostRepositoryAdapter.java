package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.repositories.shipping_cost;

import com.essenza.draco.modules.shipping_logistics.product_distribution.application.output.repository.ShippingCostRepository;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.CreateShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.ShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.UpdateShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.mappers.ShippingCostMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ShippingCostRepositoryAdapter implements ShippingCostRepository {

    private final JpaShippingCostRepository jpa;
    private final ShippingCostMapper mapper;

    public ShippingCostRepositoryAdapter(JpaShippingCostRepository jpa, ShippingCostMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public ShippingCostDto create(CreateShippingCostDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public ShippingCostDto update(Long id, UpdateShippingCostDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ShippingCost not found: " + id));
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
    public Optional<ShippingCostDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<ShippingCostDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}

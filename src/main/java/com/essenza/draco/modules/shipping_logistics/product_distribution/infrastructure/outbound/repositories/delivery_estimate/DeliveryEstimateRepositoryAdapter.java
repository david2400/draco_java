package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.repositories.delivery_estimate;

import com.essenza.draco.modules.shipping_logistics.product_distribution.application.output.repository.DeliveryEstimateRepository;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.CreateDeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.DeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.UpdateDeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.mappers.DeliveryEstimateMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DeliveryEstimateRepositoryAdapter implements DeliveryEstimateRepository {

    private final JpaDeliveryEstimateRepository jpa;
    private final DeliveryEstimateMapper mapper;

    public DeliveryEstimateRepositoryAdapter(JpaDeliveryEstimateRepository jpa, DeliveryEstimateMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public DeliveryEstimateDto create(CreateDeliveryEstimateDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public DeliveryEstimateDto update(Long id, UpdateDeliveryEstimateDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DeliveryEstimate not found: " + id));
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
    public Optional<DeliveryEstimateDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<DeliveryEstimateDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}

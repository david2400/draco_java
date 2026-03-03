package com.essenza.draco.modules.shipping_logistics.product_distribution.application.services;

import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.delivery_estimate.*;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.CreateDeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.DeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.UpdateDeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.repositories.delivery_estimate.DeliveryEstimateRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeliveryEstimateServiceImpl implements CreateDeliveryEstimateUseCase,
        UpdateDeliveryEstimateUseCase,
        DeleteDeliveryEstimateUseCase,
        FindDeliveryEstimateByIdUseCase,
        FindDeliveryEstimatesUseCase {

    private final DeliveryEstimateRepositoryAdapter repository;

    public DeliveryEstimateServiceImpl(DeliveryEstimateRepositoryAdapter repository) {
        this.repository = repository;
    }

    @Override
    public DeliveryEstimateDto create(CreateDeliveryEstimateDto input) {
        return repository.create(input);
    }

    @Override
    public DeliveryEstimateDto update(Long id, UpdateDeliveryEstimateDto input) {
        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeliveryEstimateDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryEstimateDto> findAll() {
        return repository.findAll();
    }
}

package com.essenza.draco.modules.shipping_logistics.product_distribution.application.services;

import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.shipping_cost.*;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.CreateShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.ShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.UpdateShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.repositories.shipping_cost.ShippingCostRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShippingCostServiceImpl implements CreateShippingCostUseCase,
        UpdateShippingCostUseCase,
        DeleteShippingCostUseCase,
        FindShippingCostByIdUseCase,
        FindShippingCostsUseCase {

    private final ShippingCostRepositoryAdapter repository;

    public ShippingCostServiceImpl(ShippingCostRepositoryAdapter repository) {
        this.repository = repository;
    }

    @Override
    public ShippingCostDto create(CreateShippingCostDto input) {
        return repository.create(input);
    }

    @Override
    public ShippingCostDto update(Long id, UpdateShippingCostDto input) {
        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShippingCostDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingCostDto> findAll() {
        return repository.findAll();
    }
}

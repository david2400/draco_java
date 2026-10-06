package com.essenza.draco.modules.shipping_logistics.product_distribution.application.services;

import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.*;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.dto.carrier.CreateCarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.dto.carrier.CarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.dto.carrier.UpdateCarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.output.repository.CarrierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarrierServiceImpl implements CreateCarrierUseCase,
        UpdateCarrierUseCase,
        DeleteCarrierUseCase,
        FindCarrierByIdUseCase,
        FindCarriersUseCase {

    private final CarrierRepository repository;

    public CarrierServiceImpl(CarrierRepository repository) {
        this.repository = repository;
    }

    @Override
    public CarrierDto create(CreateCarrierDto input) {
        return repository.create(input);
    }

    @Override
    public CarrierDto update(Long id, UpdateCarrierDto input) {
        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarrierDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarrierDto> findAll() {
        return repository.findAll();
    }
}

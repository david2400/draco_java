package com.essenza.draco.modules.shipping_logistics.product_distribution.application.output.repository;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CreateCarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.UpdateCarrierDto;

import java.util.List;
import java.util.Optional;

public interface CarrierRepository {

    CarrierDto create(CreateCarrierDto input);

    CarrierDto update(Long id, UpdateCarrierDto input);

    boolean deleteById(Long id);

    Optional<CarrierDto> findById(Long id);

    List<CarrierDto> findAll();
}

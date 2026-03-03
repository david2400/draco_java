package com.essenza.draco.modules.product_details.application.output.repository;

import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.CreateUnitMeasurementDto;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UnitMeasurementDto;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UpdateUnitMeasurementDto;

import java.util.List;
import java.util.Optional;

public interface UnitMeasurementRepository {
    UnitMeasurementDto create(CreateUnitMeasurementDto input);

    UnitMeasurementDto update(Long id, UpdateUnitMeasurementDto input);

    boolean deleteById(Long id);

    Optional<UnitMeasurementDto> findById(Long id);

    List<UnitMeasurementDto> findAll();
}

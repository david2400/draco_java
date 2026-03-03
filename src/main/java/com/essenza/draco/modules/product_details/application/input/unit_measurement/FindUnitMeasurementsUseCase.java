package com.essenza.draco.modules.product_details.application.input.unit_measurement;

import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UnitMeasurementDto;

import java.util.List;

public interface FindUnitMeasurementsUseCase {
    List<UnitMeasurementDto> findAll();
}

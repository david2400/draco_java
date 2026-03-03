package com.essenza.draco.modules.product_details.application.input.unit_measurement;

import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.CreateUnitMeasurementDto;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UnitMeasurementDto;

public interface CreateUnitMeasurementUseCase {
    UnitMeasurementDto create(CreateUnitMeasurementDto input);
}

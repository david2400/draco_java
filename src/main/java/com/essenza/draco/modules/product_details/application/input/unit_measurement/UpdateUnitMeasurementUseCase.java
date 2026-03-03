package com.essenza.draco.modules.product_details.application.input.unit_measurement;

import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UnitMeasurementDto;
import com.essenza.draco.modules.product_details.domain.dto.unit_measurement.UpdateUnitMeasurementDto;

public interface UpdateUnitMeasurementUseCase {
    UnitMeasurementDto update(Long id, UpdateUnitMeasurementDto input);
}

package com.essenza.draco.modules.product_details.domain.dto.unit_measurement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUnitMeasurementDto extends CreateUnitMeasurementDto {
    @NotNull
    @Positive
    private Long id;
}

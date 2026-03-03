package com.essenza.draco.modules.product_details.domain.dto.unit_measurement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UnitMeasurementDto extends AuditInfoDto {
    private Long id;
}

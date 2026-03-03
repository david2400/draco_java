package com.essenza.draco.modules.product_details.domain.dto.unit_measurement;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.validation.constraints.NotBlank;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUnitMeasurementDto extends AuditInfoDto {
    @NotBlank
    private String name;
}

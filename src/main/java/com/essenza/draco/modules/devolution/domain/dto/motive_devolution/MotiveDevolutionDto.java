package com.essenza.draco.modules.devolution.domain.dto.motive_devolution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MotiveDevolutionDto extends AuditInfoDto {
    private Long id;
    private String name;
    private String description;
}

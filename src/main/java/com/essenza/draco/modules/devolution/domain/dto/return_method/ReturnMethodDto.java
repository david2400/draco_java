package com.essenza.draco.modules.devolution.domain.dto.return_method;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnMethodDto extends AuditInfoDto {
    private Long id;
    private String name;
    private String description;
    private String instructions;
    private Boolean active;
}

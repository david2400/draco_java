package com.essenza.draco.modules.devolution.domain.dto.refund_method;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RefundMethodDto extends AuditInfoDto {
    private Long id;
    private String name;
    private String description;
    private String policy;
    private Boolean active;
}

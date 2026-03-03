package com.essenza.draco.modules.sales.domain.dto.payment_type;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTypeDto extends AuditInfoDto {
    private Long id;
    private String name;
}

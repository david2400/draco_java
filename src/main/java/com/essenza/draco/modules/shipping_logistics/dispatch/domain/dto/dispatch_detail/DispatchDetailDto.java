package com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_detail;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchDetailDto extends AuditInfoDto {
    private Long id;
    private Long dispatchProductId;
    private Long productOrderId;
}

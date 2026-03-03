package com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchProductDto extends AuditInfoDto {
    private Long id;
    private String guideNumber;
    private String address;
    private LocalDate estimatedDeliveryDate;
    private LocalDate realDeliveryDate;
    private String departmentOrigin;
    private String cityOrigin;
    private String departmentDestination;
    private String cityDestination;
    private Long orderId;
}

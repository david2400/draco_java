package com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryEstimateDto extends AuditInfoDto {
    private Long id;
    private Long carrierId;
    private Long shippingCostId;
    private String originAddress;
    private String destinationAddress;
    private LocalDateTime shipmentDate;
    private LocalDate estimatedDeliveryDate;
    private LocalDate minDeliveryDate;
    private LocalDate maxDeliveryDate;
    private Integer estimatedDays;
    private String calculationMethod;
    private Boolean isBusinessDaysOnly;
}

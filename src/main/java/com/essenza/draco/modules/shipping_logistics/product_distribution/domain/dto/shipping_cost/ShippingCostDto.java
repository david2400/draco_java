package com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingCostDto extends AuditInfoDto {
    private Long id;
    private Long carrierId;
    private String originAddress;
    private String destinationAddress;
    private BigDecimal distance;
    private BigDecimal weight;
    private BigDecimal volume;
    private BigDecimal calculatedCost;
    private String calculationMethod;
    private Boolean isEstimated;
}

package com.essenza.draco.modules.analytics.domain.dto.sales_analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SalesFunnelStepDto {
    private String stepName;
    private Integer count;
}

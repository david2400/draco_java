package com.essenza.draco.modules.analytics.domain.dto.sales_analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SalesPerformanceKpiDto {
    private BigDecimal revenueGrowth;
    private BigDecimal orderGrowth;
    private BigDecimal customerGrowth;
    private BigDecimal averageOrderValueGrowth;
    private BigDecimal conversionRateImprovement;
}

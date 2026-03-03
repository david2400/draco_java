package com.essenza.draco.modules.analytics.domain.dto.sales_analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SalesCohortAnalysisDto {
    private String cohortMonth;
    private Map<String, BigDecimal> retentionByMonth;
}

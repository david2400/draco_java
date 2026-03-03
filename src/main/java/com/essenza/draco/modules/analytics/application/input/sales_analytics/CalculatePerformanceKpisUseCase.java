package com.essenza.draco.modules.analytics.application.input.sales_analytics;

import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesPerformanceKpiDto;

import java.time.LocalDate;

public interface CalculatePerformanceKpisUseCase {
    SalesPerformanceKpiDto calculatePerformanceKpis(LocalDate startDate, LocalDate endDate);
}

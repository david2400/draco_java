package com.essenza.draco.modules.analytics.application.input.sales_analytics;

import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsDto;

import java.time.LocalDate;
import java.util.List;

public interface GenerateSalesTrendsUseCase {
    List<SalesAnalyticsDto> generateSalesTrends(LocalDate startDate, LocalDate endDate, String period);
}

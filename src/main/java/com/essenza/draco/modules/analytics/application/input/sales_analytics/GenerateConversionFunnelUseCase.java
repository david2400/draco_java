package com.essenza.draco.modules.analytics.application.input.sales_analytics;

import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesConversionFunnelDto;

import java.time.LocalDate;

public interface GenerateConversionFunnelUseCase {
    SalesConversionFunnelDto generateConversionFunnel(LocalDate startDate, LocalDate endDate);
}

package com.essenza.draco.modules.analytics.application.input.sales_analytics_record;

import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsRecordDto;

import java.util.Optional;

public interface FindSalesAnalyticsRecordByIdUseCase {
    Optional<SalesAnalyticsRecordDto> findById(Long id);
}

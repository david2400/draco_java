package com.essenza.draco.modules.analytics.application.input.sales_analytics_record;

import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsRecordDto;

import java.util.List;

public interface FindSalesAnalyticsRecordsUseCase {
    List<SalesAnalyticsRecordDto> findAll();
}

package com.essenza.draco.modules.analytics.application.input.sales_analytics_record;

import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.CreateSalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsRecordDto;

public interface CreateSalesAnalyticsRecordUseCase {
    SalesAnalyticsRecordDto create(CreateSalesAnalyticsRecordDto input);
}

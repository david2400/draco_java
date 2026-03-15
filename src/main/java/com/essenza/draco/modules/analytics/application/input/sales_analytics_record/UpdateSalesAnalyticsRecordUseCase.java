package com.essenza.draco.modules.analytics.application.input.sales_analytics_record;

import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.UpdateSalesAnalyticsRecordDto;

public interface UpdateSalesAnalyticsRecordUseCase {
    SalesAnalyticsRecordDto update(Long id, UpdateSalesAnalyticsRecordDto input);
}

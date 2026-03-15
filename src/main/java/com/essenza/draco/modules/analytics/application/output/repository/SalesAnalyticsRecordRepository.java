package com.essenza.draco.modules.analytics.application.output.repository;

import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.CreateSalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.UpdateSalesAnalyticsRecordDto;

import java.util.List;
import java.util.Optional;

public interface SalesAnalyticsRecordRepository {

    SalesAnalyticsRecordDto create(CreateSalesAnalyticsRecordDto input);

    SalesAnalyticsRecordDto update(Long id, UpdateSalesAnalyticsRecordDto input);

    boolean deleteById(Long id);

    Optional<SalesAnalyticsRecordDto> findById(Long id);

    List<SalesAnalyticsRecordDto> findAll();
}

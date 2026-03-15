package com.essenza.draco.modules.analytics.application.services;

import com.essenza.draco.modules.analytics.application.input.sales_analytics_record.CreateSalesAnalyticsRecordUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics_record.DeleteSalesAnalyticsRecordUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics_record.FindSalesAnalyticsRecordByIdUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics_record.FindSalesAnalyticsRecordsUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics_record.UpdateSalesAnalyticsRecordUseCase;
import com.essenza.draco.modules.analytics.application.output.repository.SalesAnalyticsRecordRepository;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.CreateSalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.UpdateSalesAnalyticsRecordDto;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SalesAnalyticsRecordService implements CreateSalesAnalyticsRecordUseCase,
        UpdateSalesAnalyticsRecordUseCase,
        DeleteSalesAnalyticsRecordUseCase,
        FindSalesAnalyticsRecordByIdUseCase,
        FindSalesAnalyticsRecordsUseCase {

    private final SalesAnalyticsRecordRepository repository;

    public SalesAnalyticsRecordService(SalesAnalyticsRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public SalesAnalyticsRecordDto create(CreateSalesAnalyticsRecordDto input) {
        return repository.create(input);
    }

    @Override
    public SalesAnalyticsRecordDto update(Long id, UpdateSalesAnalyticsRecordDto input) {
        repository.findById(id).orElseThrow(() -> new NotFoundException("Sales analytics record not found: " + id));
        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SalesAnalyticsRecordDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesAnalyticsRecordDto> findAll() {
        return repository.findAll();
    }
}

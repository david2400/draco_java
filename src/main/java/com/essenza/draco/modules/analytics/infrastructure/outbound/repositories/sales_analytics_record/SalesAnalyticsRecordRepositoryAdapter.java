package com.essenza.draco.modules.analytics.infrastructure.outbound.repositories.sales_analytics_record;

import com.essenza.draco.modules.analytics.application.output.repository.SalesAnalyticsRecordRepository;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.CreateSalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.UpdateSalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.infrastructure.outbound.mappers.SalesAnalyticsRecordMapper;
import com.essenza.draco.modules.analytics.infrastructure.outbound.persistence.mysql.JpaSalesAnalyticsRecordRepository;
import com.essenza.draco.modules.analytics.infrastructure.outbound.persistence.mysql.SalesAnalyticsRecordEntity;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SalesAnalyticsRecordRepositoryAdapter implements SalesAnalyticsRecordRepository {

    private final JpaSalesAnalyticsRecordRepository jpa;
    private final SalesAnalyticsRecordMapper mapper;

    public SalesAnalyticsRecordRepositoryAdapter(JpaSalesAnalyticsRecordRepository jpa,
                                                 SalesAnalyticsRecordMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public SalesAnalyticsRecordDto create(CreateSalesAnalyticsRecordDto input) {
        SalesAnalyticsRecordEntity entity = mapper.toEntity(input);
        SalesAnalyticsRecordEntity saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public SalesAnalyticsRecordDto update(Long id, UpdateSalesAnalyticsRecordDto input) {
        SalesAnalyticsRecordEntity entity = jpa.findById(id)
                .orElseThrow(() -> new NotFoundException("Sales analytics record not found: " + id));
        mapper.updateEntityFromDto(input, entity);
        SalesAnalyticsRecordEntity updated = jpa.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) {
            return false;
        }
        jpa.deleteById(id);
        return true;
    }

    @Override
    public Optional<SalesAnalyticsRecordDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<SalesAnalyticsRecordDto> findAll() {
        return mapper.toDtoList(jpa.findAll());
    }
}

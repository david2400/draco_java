package com.essenza.draco.modules.analytics.infrastructure.outbound.mappers;

import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.CreateSalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.UpdateSalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.infrastructure.outbound.persistence.mysql.SalesAnalyticsRecordEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SalesAnalyticsRecordMapper {

    SalesAnalyticsRecordDto toDto(SalesAnalyticsRecordEntity entity);

    List<SalesAnalyticsRecordDto> toDtoList(List<SalesAnalyticsRecordEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usrCrea", ignore = true)
    @Mapping(target = "usrMod", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SalesAnalyticsRecordEntity toEntity(CreateSalesAnalyticsRecordDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usrCrea", ignore = true)
    @Mapping(target = "usrMod", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(UpdateSalesAnalyticsRecordDto dto, @MappingTarget SalesAnalyticsRecordEntity entity);
}

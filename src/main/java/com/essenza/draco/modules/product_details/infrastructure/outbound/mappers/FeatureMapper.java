package com.essenza.draco.modules.product_details.infrastructure.outbound.mappers;

import com.essenza.draco.modules.product_details.domain.dto.feature.CreateFeatureDto;
import com.essenza.draco.modules.product_details.domain.dto.feature.FeatureDto;
import com.essenza.draco.modules.product_details.domain.dto.feature.UpdateFeatureDto;
import com.essenza.draco.modules.product_details.infrastructure.outbound.persistence.mysql.shop.FeatureEntity;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FeatureMapper {
    FeatureDto toDto(FeatureEntity entity);
    List<FeatureDto> toDtoList(List<FeatureEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "unitMeasurements", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    FeatureEntity toEntity(CreateFeatureDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "unitMeasurements", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateFeatureDto dto, @MappingTarget FeatureEntity entity);
}

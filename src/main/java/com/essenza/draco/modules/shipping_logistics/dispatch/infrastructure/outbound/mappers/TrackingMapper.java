package com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.mappers;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.tracking.CreateTrackingDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.tracking.TrackingDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.tracking.UpdateTrackingDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.persistence.mysql.shop.TrackingEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrackingMapper {
    TrackingDto toDto(TrackingEntity entity);
    List<TrackingDto> toDtoList(List<TrackingEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "dispatchProduct", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    TrackingEntity toEntity(CreateTrackingDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "dispatchProduct", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateTrackingDto dto, @MappingTarget TrackingEntity entity);
}

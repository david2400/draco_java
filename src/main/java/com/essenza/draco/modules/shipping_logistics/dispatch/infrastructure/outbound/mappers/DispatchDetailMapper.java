package com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.mappers;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_detail.CreateDispatchDetailDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_detail.DispatchDetailDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_detail.UpdateDispatchDetailDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.persistence.mysql.shop.DispatchDetailEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring"
)
public interface DispatchDetailMapper {
    DispatchDetailDto toDto(DispatchDetailEntity entity);
    List<DispatchDetailDto> toDtoList(List<DispatchDetailEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "dispatchProduct", ignore = true)
//    @Mapping(target = "productOrder", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    DispatchDetailEntity toEntity(CreateDispatchDetailDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "dispatchProduct", ignore = true)
//    @Mapping(target = "productOrder", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateDispatchDetailDto dto, @MappingTarget DispatchDetailEntity entity);
}

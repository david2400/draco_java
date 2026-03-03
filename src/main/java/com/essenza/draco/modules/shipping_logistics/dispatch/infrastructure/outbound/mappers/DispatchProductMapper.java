package com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.mappers;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.CreateDispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.DispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.UpdateDispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.persistence.mysql.shop.DispatchProductEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DispatchProductMapper {
    DispatchProductDto toDto(DispatchProductEntity entity);
    List<DispatchProductDto> toDtoList(List<DispatchProductEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "order", ignore = true)
//    @Mapping(target = "dispatchDetail", ignore = true)
//    @Mapping(target = "tracking", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    DispatchProductEntity toEntity(CreateDispatchProductDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "order", ignore = true)
//    @Mapping(target = "dispatchDetail", ignore = true)
//    @Mapping(target = "tracking", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateDispatchProductDto dto, @MappingTarget DispatchProductEntity entity);
}

package com.essenza.draco.modules.devolution.infrastructure.outbound.mappers;

import com.essenza.draco.modules.devolution.domain.dto.order_devolution.CreateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.OrderDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution.UpdateOrderDevolutionDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.OrderDevolutionEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderDevolutionMapper {
    OrderDevolutionDto toDto(OrderDevolutionEntity entity);
    List<OrderDevolutionDto> toDtoList(List<OrderDevolutionEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "motiveDevolution", ignore = true)
//    @Mapping(target = "order", ignore = true)
//    @Mapping(target = "returnMethod", ignore = true)
//    @Mapping(target = "refundMethod", ignore = true)
//    @Mapping(target = "orderDevolutionDetail", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    OrderDevolutionEntity toEntity(CreateOrderDevolutionDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "motiveDevolution", ignore = true)
//    @Mapping(target = "order", ignore = true)
//    @Mapping(target = "returnMethod", ignore = true)
//    @Mapping(target = "refundMethod", ignore = true)
//    @Mapping(target = "orderDevolutionDetail", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateOrderDevolutionDto dto, @MappingTarget OrderDevolutionEntity entity);
}

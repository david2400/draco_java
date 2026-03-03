package com.essenza.draco.modules.sales.infrastructure.outbound.mappers;

import com.essenza.draco.modules.sales.domain.dto.order.CreateOrderDto;
import com.essenza.draco.modules.sales.domain.dto.order.OrderDto;
import com.essenza.draco.modules.sales.domain.dto.order.UpdateOrderDto;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.OrderEntity;
import org.mapstruct.*;
import com.essenza.draco.shared.common.domain.mapper.NumberMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NumberMapper.class)
public interface OrderMapper {
    OrderDto toDto(OrderEntity entity);
    List<OrderDto> toDtoList(List<OrderEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "productOrder", ignore = true)
//    @Mapping(target = "orderDevolution", ignore = true)
//    @Mapping(target = "dispatchProduct", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    OrderEntity toEntity(CreateOrderDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "productOrder", ignore = true)
//    @Mapping(target = "orderDevolution", ignore = true)
//    @Mapping(target = "dispatchProduct", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateOrderDto dto, @MappingTarget OrderEntity entity);
}

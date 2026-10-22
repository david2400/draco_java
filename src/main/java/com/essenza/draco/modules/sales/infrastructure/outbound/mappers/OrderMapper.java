package com.essenza.draco.modules.sales.infrastructure.outbound.mappers;

import com.essenza.draco.modules.sales.application.dto.order.CreateOrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.UpdateOrderDto;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.OrderEntity;
import org.mapstruct.*;
import com.essenza.draco.shared.common.domain.mapper.NumberMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NumberMapper.class)
public interface OrderMapper {
    // V16: nombres de tienda, bodega y transportadora, y los totales de cobro y preparación los
    // completa OrderInsightsAdapter (consultas aparte, sin asociaciones entre módulos).
    @org.mapstruct.Mapping(target = "nextStates", ignore = true)
    @org.mapstruct.Mapping(target = "pickupStoreName", ignore = true)
    @org.mapstruct.Mapping(target = "carrierName", ignore = true)
    @org.mapstruct.Mapping(target = "storeName", ignore = true)
    @org.mapstruct.Mapping(target = "warehouseName", ignore = true)
    @org.mapstruct.Mapping(target = "paymentStatus", ignore = true)
    @org.mapstruct.Mapping(target = "paidAmount", ignore = true)
    @org.mapstruct.Mapping(target = "balanceDue", ignore = true)
    @org.mapstruct.Mapping(target = "itemsCount", ignore = true)
    @org.mapstruct.Mapping(target = "unitsCount", ignore = true)
    @org.mapstruct.Mapping(target = "pickedUnits", ignore = true)
    @org.mapstruct.Mapping(target = "itemsSubtotal", ignore = true)
    @org.mapstruct.Mapping(target = "overdue", ignore = true)
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

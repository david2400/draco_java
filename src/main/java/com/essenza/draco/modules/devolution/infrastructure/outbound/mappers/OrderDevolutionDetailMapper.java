package com.essenza.draco.modules.devolution.infrastructure.outbound.mappers;

import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.CreateOrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.OrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail.UpdateOrderDevolutionDetailDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.OrderDevolutionDetailEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderDevolutionDetailMapper {
    OrderDevolutionDetailDto toDto(OrderDevolutionDetailEntity entity);
    List<OrderDevolutionDetailDto> toDtoList(List<OrderDevolutionDetailEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "orderDevolution", ignore = true)
//    @Mapping(target = "productOrder", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    OrderDevolutionDetailEntity toEntity(CreateOrderDevolutionDetailDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "orderDevolution", ignore = true)
//    @Mapping(target = "productOrder", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateOrderDevolutionDetailDto dto, @MappingTarget OrderDevolutionDetailEntity entity);
}

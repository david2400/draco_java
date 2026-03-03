package com.essenza.draco.modules.sales.infrastructure.outbound.mappers;

import com.essenza.draco.modules.sales.domain.dto.product_order.CreateProductOrderDto;
import com.essenza.draco.modules.sales.domain.dto.product_order.ProductOrderDto;
import com.essenza.draco.modules.sales.domain.dto.product_order.UpdateProductOrderDto;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.ProductOrderEntity;
import org.mapstruct.*;
import com.essenza.draco.shared.common.domain.mapper.NumberMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NumberMapper.class)
public interface ProductOrderMapper {
    ProductOrderDto toDto(ProductOrderEntity entity);
    List<ProductOrderDto> toDtoList(List<ProductOrderEntity> entities);
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "product", ignore = true)
//    @Mapping(target = "order", ignore = true)
//    @Mapping(target = "orderDevolution", ignore = true)
//    @Mapping(target = "dispatchDetail", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    ProductOrderEntity toEntity(CreateProductOrderDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "product", ignore = true)
//    @Mapping(target = "order", ignore = true)
//    @Mapping(target = "orderDevolution", ignore = true)
//    @Mapping(target = "dispatchDetail", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateProductOrderDto dto, @MappingTarget ProductOrderEntity entity);
}

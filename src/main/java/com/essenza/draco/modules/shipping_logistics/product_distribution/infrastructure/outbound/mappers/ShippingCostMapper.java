package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.mappers;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.CreateShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.ShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.UpdateShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.persistence.mysql.shop.ShippingCostEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShippingCostMapper {
    ShippingCostDto toDto(ShippingCostEntity entity);
    List<ShippingCostDto> toDtoList(List<ShippingCostEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "carrier", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    ShippingCostEntity toEntity(CreateShippingCostDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "carrier", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateShippingCostDto dto, @MappingTarget ShippingCostEntity entity);
}

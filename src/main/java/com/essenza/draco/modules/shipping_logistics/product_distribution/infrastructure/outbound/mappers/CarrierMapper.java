package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.mappers;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CreateCarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.UpdateCarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.persistence.mysql.shop.CarrierEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarrierMapper {
    CarrierDto toDto(CarrierEntity entity);
    List<CarrierDto> toDtoList(List<CarrierEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    CarrierEntity toEntity(CreateCarrierDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateCarrierDto dto, @MappingTarget CarrierEntity entity);
}

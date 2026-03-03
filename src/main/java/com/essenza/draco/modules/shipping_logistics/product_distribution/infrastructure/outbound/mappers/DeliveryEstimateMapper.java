package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.mappers;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.CreateDeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.DeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.UpdateDeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.persistence.mysql.shop.DeliveryEstimateEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryEstimateMapper {
    DeliveryEstimateDto toDto(DeliveryEstimateEntity entity);
    List<DeliveryEstimateDto> toDtoList(List<DeliveryEstimateEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "carrier", ignore = true)
//    @Mapping(target = "shippingCost", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    DeliveryEstimateEntity toEntity(CreateDeliveryEstimateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "carrier", ignore = true)
//    @Mapping(target = "shippingCost", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateDeliveryEstimateDto dto, @MappingTarget DeliveryEstimateEntity entity);
}

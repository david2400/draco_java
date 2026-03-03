package com.essenza.draco.modules.sales.infrastructure.outbound.mappers;

import com.essenza.draco.modules.sales.domain.dto.payment_type.CreatePaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.PaymentTypeDto;
import com.essenza.draco.modules.sales.domain.dto.payment_type.UpdatePaymentTypeDto;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.PaymentTypeEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentTypeMapper {
    PaymentTypeDto toDto(PaymentTypeEntity entity);
    List<PaymentTypeDto> toDtoList(List<PaymentTypeEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    PaymentTypeEntity toEntity(CreatePaymentTypeDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdatePaymentTypeDto dto, @MappingTarget PaymentTypeEntity entity);
}

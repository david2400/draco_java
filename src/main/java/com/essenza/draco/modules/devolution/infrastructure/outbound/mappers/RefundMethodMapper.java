package com.essenza.draco.modules.devolution.infrastructure.outbound.mappers;

import com.essenza.draco.modules.devolution.domain.dto.refund_method.CreateRefundMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.RefundMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.refund_method.UpdateRefundMethodDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.RefundMethodEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RefundMethodMapper {
    RefundMethodDto toDto(RefundMethodEntity entity);
    List<RefundMethodDto> toDtoList(List<RefundMethodEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    RefundMethodEntity toEntity(CreateRefundMethodDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateRefundMethodDto dto, @MappingTarget RefundMethodEntity entity);
}

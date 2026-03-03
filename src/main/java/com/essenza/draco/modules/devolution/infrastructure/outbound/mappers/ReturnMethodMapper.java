package com.essenza.draco.modules.devolution.infrastructure.outbound.mappers;

import com.essenza.draco.modules.devolution.domain.dto.return_method.CreateReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.ReturnMethodDto;
import com.essenza.draco.modules.devolution.domain.dto.return_method.UpdateReturnMethodDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.ReturnMethodEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReturnMethodMapper {
    ReturnMethodDto toDto(ReturnMethodEntity entity);
    List<ReturnMethodDto> toDtoList(List<ReturnMethodEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    ReturnMethodEntity toEntity(CreateReturnMethodDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateReturnMethodDto dto, @MappingTarget ReturnMethodEntity entity);
}

package com.essenza.draco.modules.devolution.infrastructure.outbound.mappers;

import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.CreateMotiveDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.MotiveDevolutionDto;
import com.essenza.draco.modules.devolution.domain.dto.motive_devolution.UpdateMotiveDevolutionDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.MotiveDevolutionEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MotiveDevolutionMapper {
    MotiveDevolutionDto toDto(MotiveDevolutionEntity entity);
    List<MotiveDevolutionDto> toDtoList(List<MotiveDevolutionEntity> entities);

    // @Mapping(target = "id", ignore = true)
    // @Mapping(target = "orderDevolution", ignore = true)
    // @Mapping(target = "usrCrea", ignore = true)
    // @Mapping(target = "usrMod", ignore = true)
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    MotiveDevolutionEntity toEntity(CreateMotiveDevolutionDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "orderDevolution", ignore = true)
    // @Mapping(target = "usrCrea", ignore = true)
    // @Mapping(target = "usrMod", ignore = true)
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateMotiveDevolutionDto dto, @MappingTarget MotiveDevolutionEntity entity);
}

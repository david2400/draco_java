package com.essenza.draco.modules.devolution.infrastructure.outbound.mappers;

import com.essenza.draco.modules.devolution.domain.dto.evidence.CreateEvidenceDto;
import com.essenza.draco.modules.devolution.domain.dto.evidence.EvidenceDto;
import com.essenza.draco.modules.devolution.domain.dto.evidence.UpdateEvidenceDto;
import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.OrderDevolutionEvidenceEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EvidenceMapper {
    EvidenceDto toDto(OrderDevolutionEvidenceEntity entity);
    List<EvidenceDto> toDtoList(List<OrderDevolutionEvidenceEntity> entities);

    // @Mapping(target = "id", ignore = true)
    // @Mapping(target = "orderDevolution", ignore = true)
    // @Mapping(target = "usrCrea", ignore = true)
    // @Mapping(target = "usrMod", ignore = true)
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    OrderDevolutionEvidenceEntity toEntity(CreateEvidenceDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "orderDevolution", ignore = true)
    // @Mapping(target = "usrCrea", ignore = true)
    // @Mapping(target = "usrMod", ignore = true)
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateEvidenceDto dto, @MappingTarget OrderDevolutionEvidenceEntity entity);
}

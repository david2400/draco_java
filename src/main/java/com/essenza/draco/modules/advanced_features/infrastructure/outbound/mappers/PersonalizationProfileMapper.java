package com.essenza.draco.modules.advanced_features.infrastructure.outbound.mappers;

import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.CreatePersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.PersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile.UpdatePersonalizationProfileDto;
import com.essenza.draco.modules.advanced_features.infrastructure.outbound.persistence.mysql.PersonalizationProfileEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonalizationProfileMapper {

    PersonalizationProfileDto toDto(PersonalizationProfileEntity entity);

    List<PersonalizationProfileDto> toDtoList(List<PersonalizationProfileEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usrCrea", ignore = true)
    @Mapping(target = "usrMod", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    PersonalizationProfileEntity toEntity(CreatePersonalizationProfileDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usrCrea", ignore = true)
    @Mapping(target = "usrMod", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(UpdatePersonalizationProfileDto dto,
                             @MappingTarget PersonalizationProfileEntity entity);
}

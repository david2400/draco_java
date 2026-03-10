package com.essenza.draco.modules.cms.infrastructure.outbound.mappers;

import com.essenza.draco.modules.cms.domain.dto.page.CreatePageDto;
import com.essenza.draco.modules.cms.domain.dto.page.PageDto;
import com.essenza.draco.modules.cms.domain.dto.page.UpdatePageDto;
import com.essenza.draco.modules.cms.infrastructure.outbound.persistence.mysql.PageEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PageMapper {

    PageDto toDto(PageEntity entity);

    List<PageDto> toDtoList(List<PageEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usrCrea", ignore = true)
    @Mapping(target = "usrMod", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    PageEntity toEntity(CreatePageDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usrCrea", ignore = true)
    @Mapping(target = "usrMod", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(UpdatePageDto dto, @MappingTarget PageEntity entity);
}

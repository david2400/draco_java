package com.essenza.draco.modules.catalog.infrastructure.outbound.mappers;

import com.essenza.draco.modules.catalog.domain.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.UpdateSubcategoryDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.SubcategoryEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubcategoryMapper {
    SubcategoryDto toDto(SubcategoryEntity entity);
    List<SubcategoryDto> toDtoList(List<SubcategoryEntity> entities);

    // @Mapping(target = "id", ignore = true)
    // @Mapping(target = "category", ignore = true)
    // @Mapping(target = "usrCrea", ignore = true)
    // @Mapping(target = "usrMod", ignore = true)
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    SubcategoryEntity toEntity(CreateSubcategoryDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "category", ignore = true)
    // @Mapping(target = "usrCrea", ignore = true)
    // @Mapping(target = "usrMod", ignore = true)
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateSubcategoryDto dto, @MappingTarget SubcategoryEntity entity);
}

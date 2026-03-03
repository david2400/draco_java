package com.essenza.draco.modules.catalog.infrastructure.outbound.mappers;

import com.essenza.draco.modules.catalog.domain.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.CreateCategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.UpdateCategoryDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.CategoryEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(CategoryEntity entity);
    List<CategoryDto> toDtoList(List<CategoryEntity> entities);

    // @Mapping(target = "id", ignore = true)
    // @Mapping(target = "usrCrea", ignore = true)
    // @Mapping(target = "usrMod", ignore = true)
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    CategoryEntity toEntity(CreateCategoryDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "usrCrea", ignore = true)
    // @Mapping(target = "usrMod", ignore = true)
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateCategoryDto dto, @MappingTarget CategoryEntity entity);
}

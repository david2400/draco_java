package com.essenza.draco.modules.catalog.infrastructure.outbound.mappers;

import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.CreateBrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.UpdateBrandDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.BrandEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    BrandDto toDto(BrandEntity entity);
    List<BrandDto> toDtoList(List<BrandEntity> entities);

    // @Mapping(target = "id", ignore = true)
    // @Mapping(target = "usrCrea", ignore = true)
    // @Mapping(target = "usrMod", ignore = true)
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    BrandEntity toEntity(CreateBrandDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "usrCrea", ignore = true)
    // @Mapping(target = "usrMod", ignore = true)
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateBrandDto dto, @MappingTarget BrandEntity entity);
}

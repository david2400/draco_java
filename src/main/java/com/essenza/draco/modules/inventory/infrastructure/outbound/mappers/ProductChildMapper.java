package com.essenza.draco.modules.inventory.infrastructure.outbound.mappers;

import com.essenza.draco.modules.inventory.domain.dto.product_child.CreateProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.ProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.UpdateProductChildDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductChildEntity;
import org.mapstruct.*;
import com.essenza.draco.shared.common.domain.mapper.NumberMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NumberMapper.class)
public interface ProductChildMapper {
    ProductChildDto toDto(ProductChildEntity entity);
    List<ProductChildDto> toDtoList(List<ProductChildEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "parent", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    ProductChildEntity toEntity(CreateProductChildDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "parent", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateProductChildDto dto, @MappingTarget ProductChildEntity entity);
}

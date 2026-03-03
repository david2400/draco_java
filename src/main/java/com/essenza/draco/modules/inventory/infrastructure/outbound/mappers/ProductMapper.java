package com.essenza.draco.modules.inventory.infrastructure.outbound.mappers;

import com.essenza.draco.modules.inventory.domain.dto.product.CreateProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.UpdateProductDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductEntity;
import org.mapstruct.*;
import com.essenza.draco.shared.common.domain.mapper.NumberMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NumberMapper.class)
public interface ProductMapper {
    ProductDto toDto(ProductEntity entity);
    List<ProductDto> toDtoList(List<ProductEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "brand", ignore = true)
//    @Mapping(target = "category", ignore = true)
//    @Mapping(target = "subcategory", ignore = true)
//    @Mapping(target = "supplier", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    ProductEntity toEntity(CreateProductDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "brand", ignore = true)
//    @Mapping(target = "category", ignore = true)
//    @Mapping(target = "subcategory", ignore = true)
//    @Mapping(target = "supplier", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateProductDto dto, @MappingTarget ProductEntity entity);
}

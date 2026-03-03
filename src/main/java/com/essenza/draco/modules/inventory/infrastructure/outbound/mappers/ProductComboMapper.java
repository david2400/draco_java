package com.essenza.draco.modules.inventory.infrastructure.outbound.mappers;

import com.essenza.draco.modules.inventory.domain.dto.product_combo.CreateProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.ProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.UpdateProductComboDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductComboEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductComboMapper {
    ProductComboDto toDto(ProductComboEntity entity);
    List<ProductComboDto> toDtoList(List<ProductComboEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "product", ignore = true)
//    @Mapping(target = "combo", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    ProductComboEntity toEntity(CreateProductComboDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "product", ignore = true)
//    @Mapping(target = "combo", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateProductComboDto dto, @MappingTarget ProductComboEntity entity);
}

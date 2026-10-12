package com.essenza.draco.modules.inventory.infrastructure.outbound.mappers;

import com.essenza.draco.modules.inventory.domain.dto.supplier.CreateSupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.SupplierDto;
import com.essenza.draco.modules.inventory.domain.dto.supplier.UpdateSupplierDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.SupplierEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierDto toDto(SupplierEntity entity);
    List<SupplierDto> toDtoList(List<SupplierEntity> entities);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    SupplierEntity toEntity(CreateSupplierDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    
    void updateEntityFromDto(UpdateSupplierDto dto, @MappingTarget SupplierEntity entity);
}

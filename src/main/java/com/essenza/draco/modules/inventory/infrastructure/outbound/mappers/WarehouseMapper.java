package com.essenza.draco.modules.inventory.infrastructure.outbound.mappers;

import com.essenza.draco.modules.inventory.domain.dto.warehouse.CreateWarehouseDto;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.UpdateWarehouseDto;
import com.essenza.draco.modules.inventory.domain.dto.warehouse.WarehouseDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.WarehouseEntity;
import org.mapstruct.*;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    WarehouseDto toDto(WarehouseEntity entity);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    WarehouseEntity toEntity(CreateWarehouseDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "usrCrea", ignore = true)
//    @Mapping(target = "usrMod", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateWarehouseDto dto, @MappingTarget WarehouseEntity entity);
}

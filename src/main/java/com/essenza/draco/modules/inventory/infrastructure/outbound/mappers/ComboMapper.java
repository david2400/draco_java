// package com.essenza.draco.modules.inventory.infrastructure.outbound.mappers;

// import com.essenza.draco.modules.inventory.domain.dto.combo.ComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.combo.CreateComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.combo.UpdateComboDto;
// import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ComboEntity;
// import org.mapstruct.*;
// import com.essenza.draco.shared.common.domain.mapper.NumberMapper;

// import java.util.List;

// @Mapper(componentModel = "spring", uses = NumberMapper.class)
// public interface ComboMapper {
//     ComboDto toDto(ComboEntity entity);

//     List<ComboDto> toDtoList(List<ComboEntity> entities);

//     @Mapping(target = "id", ignore = true)
//     @Mapping(target = "productCombo", ignore = true)
//     @Mapping(target = "usrCrea", ignore = true)
//     @Mapping(target = "usrMod", ignore = true)
//     @Mapping(target = "createdAt", ignore = true)
//     @Mapping(target = "updatedAt", ignore = true)
//     ComboEntity toEntity(CreateComboDto dto);

//     @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//     @Mapping(target = "productCombo", ignore = true)
//     @Mapping(target = "usrCrea", ignore = true)
//     @Mapping(target = "usrMod", ignore = true)
//     @Mapping(target = "createdAt", ignore = true)
//     @Mapping(target = "updatedAt", ignore = true)
//     void updateEntityFromDto(UpdateComboDto dto, @MappingTarget ComboEntity entity);
// }

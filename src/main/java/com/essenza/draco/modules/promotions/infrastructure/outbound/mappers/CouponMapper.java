package com.essenza.draco.modules.promotions.infrastructure.outbound.mappers;

import com.essenza.draco.modules.promotions.domain.dto.coupon.CouponDto;
import com.essenza.draco.modules.promotions.domain.dto.coupon.CreateCouponDto;
import com.essenza.draco.modules.promotions.domain.dto.coupon.UpdateCouponDto;
import com.essenza.draco.modules.promotions.infrastructure.outbound.persistence.mysql.shop.CouponEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    CouponDto toDto(CouponEntity entity);

    List<CouponDto> toDtoList(List<CouponEntity> entities);

    CouponEntity toEntity(CreateCouponDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateCouponDto dto, @MappingTarget CouponEntity entity);
}

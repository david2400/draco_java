package com.essenza.draco.modules.promotions.infrastructure.outbound.persistence.mysql.shop;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.essenza.draco.modules.promotions.application.dto.coupon.CreateCouponDto;
import com.essenza.draco.modules.promotions.infrastructure.outbound.mappers.CouponMapper;

/** Un cupón recién creado debe tener usage_count = 0 (columna NOT NULL). */
class CouponEntityDefaultsTest {

    @Test
    void elBuilderConservaElValorPorDefecto() {
        assertThat(CouponEntity.builder().code("X").build().getUsageCount()).isZero();
        assertThat(new CouponEntity().getUsageCount()).isZero();
    }

    @Test
    void alCrearDesdeElDtoNoQuedaEnNull() {
        CreateCouponDto dto = CreateCouponDto.builder()
                .code("BIENVENIDA10").name("Bienvenida").discountType("PERCENTAGE")
                .discountValue(new BigDecimal("10")).validFrom(LocalDateTime.now())
                .validUntil(LocalDateTime.now().plusDays(30)).isActive(true).isPublic(true)
                .build();
        CouponEntity entity = Mappers.getMapper(CouponMapper.class).toEntity(dto);
        assertThat(entity.getUsageCount()).isZero();
    }

    @Test
    void prePersistRellenaSiLlegaNull() {
        CouponEntity entity = CouponEntity.builder().usageCount(null).build();
        entity.defaultUsageCount();
        assertThat(entity.getUsageCount()).isZero();
    }
}

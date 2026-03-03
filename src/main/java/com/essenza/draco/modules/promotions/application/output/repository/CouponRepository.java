package com.essenza.draco.modules.promotions.application.output.repository;

import com.essenza.draco.modules.promotions.domain.dto.coupon.CouponDto;
import com.essenza.draco.modules.promotions.domain.dto.coupon.CreateCouponDto;
import com.essenza.draco.modules.promotions.domain.dto.coupon.UpdateCouponDto;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {

    CouponDto create(CreateCouponDto input);

    CouponDto update(Long id, UpdateCouponDto input);

    boolean deleteById(Long id);

    Optional<CouponDto> findById(Long id);

    List<CouponDto> findAll();

    Optional<CouponDto> findByCode(String code);

    List<CouponDto> findActiveCoupons();

    List<CouponDto> findPublicCoupons();

    boolean incrementUsageCount(Long couponId);
}

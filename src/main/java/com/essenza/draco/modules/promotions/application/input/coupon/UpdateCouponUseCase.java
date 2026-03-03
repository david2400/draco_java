package com.essenza.draco.modules.promotions.application.input.coupon;

import com.essenza.draco.modules.promotions.domain.dto.coupon.UpdateCouponDto;
import com.essenza.draco.modules.promotions.domain.dto.coupon.CouponDto;

public interface UpdateCouponUseCase {
    CouponDto update(Long id, UpdateCouponDto input);
}

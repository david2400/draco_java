package com.essenza.draco.modules.promotions.application.input.coupon;

import com.essenza.draco.modules.promotions.application.dto.coupon.CreateCouponDto;
import com.essenza.draco.modules.promotions.application.dto.coupon.CouponDto;

public interface CreateCouponUseCase {
    CouponDto create(CreateCouponDto input);
}

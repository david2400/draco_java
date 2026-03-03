package com.essenza.draco.modules.promotions.application.input.coupon;

import com.essenza.draco.modules.promotions.domain.dto.coupon.CouponDto;
import java.util.List;

public interface FindCouponsUseCase {
    List<CouponDto> findAll();
}

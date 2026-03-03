package com.essenza.draco.modules.promotions.application.input.coupon;

import com.essenza.draco.modules.promotions.domain.dto.coupon.CouponDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ValidateCouponUseCase {
    Optional<CouponDto> validateCoupon(String couponCode, BigDecimal orderAmount, List<Long> productIds, List<Long> categoryIds);
}

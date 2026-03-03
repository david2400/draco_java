package com.essenza.draco.modules.promotions.application.input.coupon;

import com.essenza.draco.modules.promotions.domain.dto.coupon.CouponDto;
import java.util.Optional;

public interface FindCouponByIdUseCase {
    Optional<CouponDto> findById(Long id);
}

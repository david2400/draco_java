package com.essenza.draco.modules.promotions.application.services;

import com.essenza.draco.modules.promotions.application.input.coupon.*;
import com.essenza.draco.modules.promotions.application.output.repository.CouponRepository;
import com.essenza.draco.modules.promotions.domain.dto.coupon.CouponDto;
import com.essenza.draco.modules.promotions.domain.dto.coupon.CreateCouponDto;
import com.essenza.draco.modules.promotions.domain.dto.coupon.UpdateCouponDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CouponServiceImpl implements CreateCouponUseCase, UpdateCouponUseCase,
        DeleteCouponUseCase, FindCouponByIdUseCase, FindCouponsUseCase {

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public CouponDto create(CreateCouponDto input) {
        return couponRepository.create(input);
    }

    @Override
    public CouponDto update(Long id, UpdateCouponDto input) {
        return couponRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return couponRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CouponDto> findById(Long id) {
        return couponRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponDto> findAll() {
        return couponRepository.findAll();
    }
}

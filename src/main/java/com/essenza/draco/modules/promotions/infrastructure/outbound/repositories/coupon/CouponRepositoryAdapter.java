package com.essenza.draco.modules.promotions.infrastructure.outbound.repositories.coupon;

import com.essenza.draco.modules.promotions.application.output.repository.CouponRepository;
import com.essenza.draco.modules.promotions.domain.dto.coupon.CouponDto;
import com.essenza.draco.modules.promotions.domain.dto.coupon.CreateCouponDto;
import com.essenza.draco.modules.promotions.domain.dto.coupon.UpdateCouponDto;
import com.essenza.draco.modules.promotions.infrastructure.outbound.mappers.CouponMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CouponRepositoryAdapter implements CouponRepository {

    private final JpaCouponRepository jpa;
    private final CouponMapper mapper;

    public CouponRepositoryAdapter(JpaCouponRepository jpa, CouponMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public CouponDto create(CreateCouponDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public CouponDto update(Long id, UpdateCouponDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found: " + id));
        mapper.updateEntityFromDto(input, entity);
        var updated = jpa.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }

    @Override
    public Optional<CouponDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<CouponDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public Optional<CouponDto> findByCode(String code) {
        return jpa.findByCode(code).map(mapper::toDto);
    }

    @Override
    public List<CouponDto> findActiveCoupons() {
        return mapper.toDtoList(jpa.findActiveCoupons(LocalDateTime.now()));
    }

    @Override
    public List<CouponDto> findPublicCoupons() {
        return mapper.toDtoList(jpa.findPublicCoupons(LocalDateTime.now()));
    }

    @Override
    @Transactional
    public boolean incrementUsageCount(Long couponId) {
        return jpa.incrementUsageCount(couponId) > 0;
    }
}

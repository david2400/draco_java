package com.essenza.draco.modules.promotions.infrastructure.outbound.repositories.coupon;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.essenza.draco.modules.promotions.infrastructure.outbound.persistence.mysql.shop.CouponEntity;
import com.essenza.draco.shared.common.promotions.Coupons;

import jakarta.persistence.EntityManager;

/** Cupones para Ventas ({@link Coupons}): condiciones y conteo de usos. */
@Component
public class CouponsAdapter implements Coupons {

    private final JpaCouponRepository coupons;
    private final EntityManager em;

    public CouponsAdapter(JpaCouponRepository coupons, EntityManager em) {
        this.coupons = coupons;
        this.em = em;
    }

    @Override
    public Optional<CouponTerms> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        String normalized = code.trim().toUpperCase(Locale.ROOT);
        return em.createQuery("select c from CouponEntity c where upper(c.code) = :code", CouponEntity.class)
                .setParameter("code", normalized).setMaxResults(1).getResultStream().findFirst().map(CouponsAdapter::toTerms);
    }

    @Override
    public Optional<CouponTerms> findById(Long id) {
        return id == null ? Optional.empty() : coupons.findById(id).map(CouponsAdapter::toTerms);
    }

    @Override
    public void redeem(Long couponId, int delta) {
        if (couponId == null || delta == 0) {
            return;
        }
        em.createQuery("update CouponEntity c set c.usageCount = case when coalesce(c.usageCount, 0) + :delta < 0 then 0 "
                        + "else coalesce(c.usageCount, 0) + :delta end where c.id = :id")
                .setParameter("delta", delta).setParameter("id", couponId).executeUpdate();
    }

    static CouponTerms toTerms(CouponEntity c) {
        return new CouponTerms(c.getId(), c.getCode(), c.getDiscountType(), c.getDiscountValue(), c.getMaximumDiscountAmount(),
                c.getMinimumOrderAmount(), c.getValidFrom(), c.getValidUntil(), Boolean.TRUE.equals(c.getIsActive()),
                c.getUsageLimit(), c.getUsageCount() == null ? 0 : c.getUsageCount(), ids(c.getApplicableProducts()),
                ids(c.getExcludedProducts()), ids(c.getApplicableCategories()), ids(c.getExcludedCategories()));
    }

    /** "1, 2,3" → {1, 2, 3}; ignora lo que no sea número. */
    static Set<Long> ids(String csv) {
        if (csv == null || csv.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(csv.split("[,;\\s]+")).map(String::trim).filter(s -> s.matches("\\d{1,18}"))
                .map(Long::valueOf).filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
    }
}

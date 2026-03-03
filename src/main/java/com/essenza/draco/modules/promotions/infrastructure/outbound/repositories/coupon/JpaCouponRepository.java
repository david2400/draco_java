package com.essenza.draco.modules.promotions.infrastructure.outbound.repositories.coupon;

import com.essenza.draco.modules.promotions.infrastructure.outbound.persistence.mysql.shop.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCouponRepository extends JpaRepository<CouponEntity, Long> {

    Optional<CouponEntity> findByCode(String code);

    @Query("SELECT c FROM CouponEntity c WHERE c.isActive = true AND c.validFrom <= :now AND c.validUntil >= :now")
    List<CouponEntity> findActiveCoupons(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM CouponEntity c WHERE c.isPublic = true AND c.isActive = true AND c.validFrom <= :now AND c.validUntil >= :now")
    List<CouponEntity> findPublicCoupons(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE CouponEntity c SET c.usageCount = c.usageCount + 1 WHERE c.id = :id")
    int incrementUsageCount(@Param("id") Long id);
}

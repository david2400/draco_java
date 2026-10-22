package com.essenza.draco.shared.common.promotions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * Contrato de Promociones para que Ventas aplique cupones en las órdenes: condiciones del
 * cupón y registro de usos. Lo implementa Promociones.
 */
public interface Coupons {

    record CouponTerms(Long id, String code, String discountType, BigDecimal discountValue, BigDecimal maximumDiscount,
                       BigDecimal minimumOrder, LocalDateTime validFrom, LocalDateTime validUntil, boolean active,
                       Integer usageLimit, int usageCount, Set<Long> products, Set<Long> excludedProducts,
                       Set<Long> categories, Set<Long> excludedCategories) {
    }

    /** Cupón por código (sin distinguir mayúsculas). */
    Optional<CouponTerms> findByCode(String code);

    Optional<CouponTerms> findById(Long id);

    /** Suma {@code delta} usos (1 al pagar la orden, −1 si se cancela después). */
    void redeem(Long couponId, int delta);
}

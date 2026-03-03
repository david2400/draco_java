package com.essenza.draco.modules.promotions.domain.dto.coupon;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto extends AuditInfoDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String discountType; // PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING
    private BigDecimal discountValue;
    private BigDecimal minimumOrderAmount;
    private BigDecimal maximumDiscountAmount;
    private Integer usageLimit;
    private Integer usageCount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean isActive;
    private Boolean isPublic;
    private String applicableCategories;
    private String applicableProducts;
    private String excludedCategories;
    private String excludedProducts;
}

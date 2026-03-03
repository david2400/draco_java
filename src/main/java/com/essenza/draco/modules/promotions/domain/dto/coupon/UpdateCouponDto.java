package com.essenza.draco.modules.promotions.domain.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateCouponDto extends CreateCouponDto {
    
    @NotNull(message = "Coupon ID is required for update")
    @Positive(message = "Coupon ID must be positive")
    private Long id;
}

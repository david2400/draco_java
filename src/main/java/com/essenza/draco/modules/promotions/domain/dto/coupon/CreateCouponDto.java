package com.essenza.draco.modules.promotions.domain.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCouponDto {
    
    @NotBlank(message = "Coupon code is required")
    @Size(min = 3, max = 50, message = "Coupon code must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Coupon code must contain only uppercase letters, numbers, underscores and hyphens")
    private String code;
    
    @NotBlank(message = "Coupon name is required")
    @Size(max = 255, message = "Coupon name cannot exceed 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Discount type is required")
    @Pattern(regexp = "^(PERCENTAGE|FIXED_AMOUNT|FREE_SHIPPING)$", message = "Discount type must be PERCENTAGE, FIXED_AMOUNT, or FREE_SHIPPING")
    private String discountType;
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Discount value cannot exceed 999999.99")
    private BigDecimal discountValue;
    
    @DecimalMin(value = "0.00", message = "Minimum order amount cannot be negative")
    private BigDecimal minimumOrderAmount;
    
    @DecimalMin(value = "0.00", message = "Maximum discount amount cannot be negative")
    private BigDecimal maximumDiscountAmount;
    
    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;
    
    @NotNull(message = "Valid from date is required")
    private LocalDateTime validFrom;
    
    @NotNull(message = "Valid until date is required")
    private LocalDateTime validUntil;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    @NotNull(message = "Public status is required")
    private Boolean isPublic;
    
    private String applicableCategories;
    private String applicableProducts;
    private String excludedCategories;
    private String excludedProducts;
}

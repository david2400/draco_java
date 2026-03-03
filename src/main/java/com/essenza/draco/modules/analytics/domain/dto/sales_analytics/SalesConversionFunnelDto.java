package com.essenza.draco.modules.analytics.domain.dto.sales_analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SalesConversionFunnelDto {
    private Integer visitors;
    private Integer productViews;
    private Integer addToCart;
    private Integer checkout;
    private Integer purchase;
    private BigDecimal visitorToViewRate;
    private BigDecimal viewToCartRate;
    private BigDecimal cartToCheckoutRate;
    private BigDecimal checkoutToPurchaseRate;
    private BigDecimal overallConversionRate;
}

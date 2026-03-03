package com.essenza.draco.modules.analytics.domain.dto.sales_analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticsDto {
    private LocalDate date;
    private String period; // DAILY, WEEKLY, MONTHLY, YEARLY
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private Integer totalOrders;
    private Integer totalCustomers;
    private Integer newCustomers;
    private Integer returningCustomers;
    private BigDecimal conversionRate;
    private BigDecimal customerAcquisitionCost;
    private BigDecimal customerLifetimeValue;
    private Map<String, BigDecimal> revenueByCategory;
    private Map<String, Integer> ordersByChannel;
    private Map<String, BigDecimal> revenueByRegion;
    private Integer totalProductsSold;
    private BigDecimal refundAmount;
    private BigDecimal refundRate;
}

package com.essenza.draco.modules.analytics.domain.dto.sales_analytics;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticsRecordDto extends AuditInfoDto {
    private Long id;
    private String reportName;
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private Integer totalOrders;
    private Integer totalCustomers;
    private Integer newCustomers;
    private Integer returningCustomers;
    private BigDecimal conversionRate;
    private BigDecimal customerAcquisitionCost;
    private BigDecimal customerLifetimeValue;
    private String revenueByCategoryJson;
    private String ordersByChannelJson;
    private String revenueByRegionJson;
    private Integer totalProductsSold;
    private BigDecimal refundAmount;
    private BigDecimal refundRate;
}

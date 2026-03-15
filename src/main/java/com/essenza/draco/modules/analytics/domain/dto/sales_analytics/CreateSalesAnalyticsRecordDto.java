package com.essenza.draco.modules.analytics.domain.dto.sales_analytics;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSalesAnalyticsRecordDto {

    @NotBlank
    @Size(max = 120)
    private String reportName;

    @NotBlank
    @Pattern(regexp = "DAILY|WEEKLY|MONTHLY|YEARLY")
    private String period;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal totalRevenue;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal averageOrderValue;

    private Integer totalOrders;

    private Integer totalCustomers;

    private Integer newCustomers;

    private Integer returningCustomers;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal conversionRate;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal customerAcquisitionCost;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal customerLifetimeValue;

    private String revenueByCategoryJson;

    private String ordersByChannelJson;

    private String revenueByRegionJson;

    private Integer totalProductsSold;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal refundAmount;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal refundRate;
}

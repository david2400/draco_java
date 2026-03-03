package com.essenza.draco.modules.analytics.application.services;

import com.essenza.draco.modules.analytics.application.input.sales_analytics.CalculatePerformanceKpisUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics.GenerateCohortAnalysisUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics.GenerateConversionFunnelUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics.GenerateSalesReportUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics.GenerateSalesTrendsUseCase;
import com.essenza.draco.modules.analytics.application.output.analytics.SalesDataAggregationPort;
import com.essenza.draco.modules.analytics.application.output.analytics.SalesKpiCalculationPort;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesCohortAnalysisDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesConversionFunnelDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesPerformanceKpiDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalesAnalyticsService implements GenerateSalesReportUseCase,
        GenerateSalesTrendsUseCase,
        CalculatePerformanceKpisUseCase,
        GenerateCohortAnalysisUseCase,
        GenerateConversionFunnelUseCase {

    private final SalesDataAggregationPort dataAggregationPort;
    private final SalesKpiCalculationPort kpiCalculationPort;

    public SalesAnalyticsService(SalesDataAggregationPort dataAggregationPort,
                                 SalesKpiCalculationPort kpiCalculationPort) {
        this.dataAggregationPort = dataAggregationPort;
        this.kpiCalculationPort = kpiCalculationPort;
    }

    @Override
    public SalesAnalyticsDto generateSalesReport(LocalDate startDate, LocalDate endDate, String period) {
        Map<String, Object> salesData = dataAggregationPort.aggregateSalesData(startDate, endDate);

        return SalesAnalyticsDto.builder()
                .date(endDate)
                .period(period)
                .totalRevenue(calculateTotalRevenue(salesData))
                .averageOrderValue(calculateAverageOrderValue(salesData))
                .totalOrders(getTotalOrders(salesData))
                .totalCustomers(getTotalCustomers(salesData))
                .newCustomers(getNewCustomers(startDate, endDate))
                .returningCustomers(getReturningCustomers(salesData))
                .conversionRate(calculateConversionRate(salesData))
                .customerAcquisitionCost(calculateCustomerAcquisitionCost(salesData))
                .customerLifetimeValue(calculateCustomerLifetimeValue(salesData))
                .revenueByCategory(getRevenueByCategory(salesData))
                .ordersByChannel(getOrdersByChannel(salesData))
                .revenueByRegion(getRevenueByRegion(salesData))
                .totalProductsSold(getTotalProductsSold(salesData))
                .refundAmount(getRefundAmount(salesData))
                .refundRate(calculateRefundRate(salesData))
                .build();
    }

    @Override
    public List<SalesAnalyticsDto> generateSalesTrends(LocalDate startDate, LocalDate endDate, String period) {
        List<SalesAnalyticsDto> trends = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            LocalDate periodEnd = calculatePeriodEnd(currentDate, period);
            if (periodEnd.isAfter(endDate)) {
                periodEnd = endDate;
            }

            trends.add(generateSalesReport(currentDate, periodEnd, period));
            currentDate = periodEnd.plusDays(1);
        }

        return trends;
    }

    @Override
    public SalesPerformanceKpiDto calculatePerformanceKpis(LocalDate startDate, LocalDate endDate) {
        SalesAnalyticsDto currentPeriod = generateSalesReport(startDate, endDate, "CUSTOM");
        SalesAnalyticsDto previousPeriod = generateSalesReport(
                startDate.minus(ChronoUnit.DAYS.between(startDate, endDate) + 1, ChronoUnit.DAYS),
                startDate.minusDays(1),
                "CUSTOM");

        return SalesPerformanceKpiDto.builder()
                .revenueGrowth(calculateGrowthRate(previousPeriod.getTotalRevenue(), currentPeriod.getTotalRevenue()))
                .orderGrowth(calculateGrowthRate(
                        BigDecimal.valueOf(previousPeriod.getTotalOrders()),
                        BigDecimal.valueOf(currentPeriod.getTotalOrders())))
                .customerGrowth(calculateGrowthRate(
                        BigDecimal.valueOf(previousPeriod.getTotalCustomers()),
                        BigDecimal.valueOf(currentPeriod.getTotalCustomers())))
                .averageOrderValueGrowth(calculateGrowthRate(
                        previousPeriod.getAverageOrderValue(),
                        currentPeriod.getAverageOrderValue()))
                .conversionRateImprovement(currentPeriod.getConversionRate()
                        .subtract(previousPeriod.getConversionRate()))
                .build();
    }

    @Override
    public List<SalesCohortAnalysisDto> generateCohortAnalysis(LocalDate startDate, LocalDate endDate) {
        Map<String, List<Long>> customerCohorts = dataAggregationPort.getCustomerCohorts(startDate, endDate);
        List<SalesCohortAnalysisDto> result = new ArrayList<>();

        for (Map.Entry<String, List<Long>> cohort : customerCohorts.entrySet()) {
            String cohortMonth = cohort.getKey();
            List<Long> customers = cohort.getValue();
            Map<String, BigDecimal> retentionByMonth = new HashMap<>();

            for (int month = 0; month <= 12; month++) {
                LocalDate retentionDate = LocalDate.parse(cohortMonth + "-01").plusMonths(month);
                if (retentionDate.isAfter(endDate)) {
                    break;
                }

                int activeCustomers = dataAggregationPort.countActiveCustomers(customers, retentionDate);
                BigDecimal retentionRate = BigDecimal.valueOf(activeCustomers)
                        .divide(BigDecimal.valueOf(Math.max(1, customers.size())), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                retentionByMonth.put("month_" + month, retentionRate);
            }

            result.add(SalesCohortAnalysisDto.builder()
                    .cohortMonth(cohortMonth)
                    .retentionByMonth(retentionByMonth)
                    .build());
        }

        return result;
    }

    @Override
    public SalesConversionFunnelDto generateConversionFunnel(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> funnelSteps = dataAggregationPort.getFunnelData(startDate, endDate);

        int visitors = funnelSteps.getOrDefault("visitors", 0);
        int productViews = funnelSteps.getOrDefault("product_views", 0);
        int addToCart = funnelSteps.getOrDefault("add_to_cart", 0);
        int checkout = funnelSteps.getOrDefault("checkout", 0);
        int purchase = funnelSteps.getOrDefault("purchase", 0);

        return SalesConversionFunnelDto.builder()
                .visitors(visitors)
                .productViews(productViews)
                .addToCart(addToCart)
                .checkout(checkout)
                .purchase(purchase)
                .visitorToViewRate(calculateConversionRate(visitors, productViews))
                .viewToCartRate(calculateConversionRate(productViews, addToCart))
                .cartToCheckoutRate(calculateConversionRate(addToCart, checkout))
                .checkoutToPurchaseRate(calculateConversionRate(checkout, purchase))
                .overallConversionRate(calculateConversionRate(visitors, purchase))
                .build();
    }

    private BigDecimal calculateTotalRevenue(Map<String, Object> salesData) {
        return (BigDecimal) salesData.getOrDefault("total_revenue", BigDecimal.ZERO);
    }

    private BigDecimal calculateAverageOrderValue(Map<String, Object> salesData) {
        BigDecimal totalRevenue = calculateTotalRevenue(salesData);
        Integer totalOrders = getTotalOrders(salesData);
        if (totalOrders == 0) {
            return BigDecimal.ZERO;
        }
        return totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
    }

    private Integer getTotalOrders(Map<String, Object> salesData) {
        return (Integer) salesData.getOrDefault("total_orders", 0);
    }

    private Integer getTotalCustomers(Map<String, Object> salesData) {
        return (Integer) salesData.getOrDefault("total_customers", 0);
    }

    private Integer getNewCustomers(LocalDate startDate, LocalDate endDate) {
        return dataAggregationPort.countNewCustomers(startDate, endDate);
    }

    private Integer getReturningCustomers(Map<String, Object> salesData) {
        return (Integer) salesData.getOrDefault("returning_customers", 0);
    }

    private BigDecimal calculateConversionRate(Map<String, Object> salesData) {
        Integer visitors = (Integer) salesData.getOrDefault("visitors", 0);
        Integer purchases = getTotalOrders(salesData);
        return calculateConversionRate(visitors, purchases);
    }

    private BigDecimal calculateConversionRate(int visitors, int conversions) {
        if (visitors == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(conversions)
                .divide(BigDecimal.valueOf(visitors), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateCustomerAcquisitionCost(Map<String, Object> salesData) {
        BigDecimal marketingSpend = (BigDecimal) salesData.getOrDefault("marketing_spend", BigDecimal.ZERO);
        Integer newCustomers = (Integer) salesData.getOrDefault("new_customers", 0);
        if (newCustomers == 0) {
            return BigDecimal.ZERO;
        }
        return marketingSpend.divide(BigDecimal.valueOf(newCustomers), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCustomerLifetimeValue(Map<String, Object> salesData) {
        return kpiCalculationPort.calculateCustomerLifetimeValue(salesData);
    }

    private Map<String, BigDecimal> getRevenueByCategory(Map<String, Object> salesData) {
        return (Map<String, BigDecimal>) salesData.getOrDefault("revenue_by_category", new HashMap<>());
    }

    private Map<String, Integer> getOrdersByChannel(Map<String, Object> salesData) {
        return (Map<String, Integer>) salesData.getOrDefault("orders_by_channel", new HashMap<>());
    }

    private Map<String, BigDecimal> getRevenueByRegion(Map<String, Object> salesData) {
        return (Map<String, BigDecimal>) salesData.getOrDefault("revenue_by_region", new HashMap<>());
    }

    private Integer getTotalProductsSold(Map<String, Object> salesData) {
        return (Integer) salesData.getOrDefault("total_products_sold", 0);
    }

    private BigDecimal getRefundAmount(Map<String, Object> salesData) {
        return (BigDecimal) salesData.getOrDefault("refund_amount", BigDecimal.ZERO);
    }

    private BigDecimal calculateRefundRate(Map<String, Object> salesData) {
        BigDecimal totalRevenue = calculateTotalRevenue(salesData);
        BigDecimal refundAmount = getRefundAmount(salesData);
        if (totalRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return refundAmount.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateGrowthRate(BigDecimal previous, BigDecimal current) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private LocalDate calculatePeriodEnd(LocalDate start, String period) {
        switch (period == null ? "" : period.toUpperCase()) {
            case "DAILY":
                return start;
            case "WEEKLY":
                return start.plusDays(6);
            case "MONTHLY":
                return start.plusMonths(1).minusDays(1);
            case "YEARLY":
                return start.plusYears(1).minusDays(1);
            default:
                return start;
        }
    }
}

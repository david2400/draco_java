package com.essenza.draco.modules.analytics.infrastructure.outbound.adapters;

import com.essenza.draco.modules.analytics.application.output.analytics.SalesKpiCalculationPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class SalesKpiCalculationAdapter implements SalesKpiCalculationPort {

    private static final BigDecimal DEFAULT_RETENTION_RATE = BigDecimal.valueOf(0.65); // 65%
    private static final BigDecimal DEFAULT_MARGIN_RATE = BigDecimal.valueOf(0.25); // 25%

    @Override
    public BigDecimal calculateCustomerLifetimeValue(Map<String, Object> salesData) {
        BigDecimal totalRevenue = getBigDecimal(salesData.get("total_revenue"));
        int totalCustomers = getInteger(salesData.get("total_customers"));
        if (totalCustomers == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal averageRevenuePerCustomer = totalRevenue
                .divide(BigDecimal.valueOf(totalCustomers), 2, RoundingMode.HALF_UP);

        BigDecimal purchaseFrequency = calculatePurchaseFrequency(salesData, totalCustomers);

        // CLV approximation = Avg Revenue per Customer * Purchase Frequency * Gross Margin * Retention factor
        return averageRevenuePerCustomer
                .multiply(purchaseFrequency)
                .multiply(DEFAULT_MARGIN_RATE)
                .multiply(DEFAULT_RETENTION_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePurchaseFrequency(Map<String, Object> salesData, int totalCustomers) {
        int totalOrders = getInteger(salesData.get("total_orders"));
        if (totalCustomers == 0) {
            return BigDecimal.ONE;
        }
        return BigDecimal.valueOf(totalOrders)
                .divide(BigDecimal.valueOf(totalCustomers), 2, RoundingMode.HALF_UP)
                .max(BigDecimal.ONE);
    }

    private BigDecimal getBigDecimal(Object value) {
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return BigDecimal.ZERO;
    }

    private int getInteger(Object value) {
        if (value instanceof Integer integer) {
            return integer;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }
}

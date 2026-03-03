package com.essenza.draco.modules.analytics.application.output.analytics;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SalesDataAggregationPort {
    Map<String, Object> aggregateSalesData(LocalDate startDate, LocalDate endDate);

    int countNewCustomers(LocalDate startDate, LocalDate endDate);

    Map<String, List<Long>> getCustomerCohorts(LocalDate startDate, LocalDate endDate);

    int countActiveCustomers(List<Long> customers, LocalDate referenceDate);

    Map<String, Integer> getFunnelData(LocalDate startDate, LocalDate endDate);
}

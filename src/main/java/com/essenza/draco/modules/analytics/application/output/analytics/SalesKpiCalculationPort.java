package com.essenza.draco.modules.analytics.application.output.analytics;

import java.math.BigDecimal;
import java.util.Map;

public interface SalesKpiCalculationPort {
    BigDecimal calculateCustomerLifetimeValue(Map<String, Object> salesData);
}

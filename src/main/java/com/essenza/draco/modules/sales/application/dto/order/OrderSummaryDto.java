package com.essenza.draco.modules.sales.application.dto.order;

import java.math.BigDecimal;
import java.util.Map;

/**
 * KPIs del listado (con los mismos filtros): ventas sin canceladas, ticket promedio y colas
 * de trabajo (por cobrar, por preparar, listas, despachadas y vencidas).
 */
public record OrderSummaryDto(long count, BigDecimal grossSales, BigDecimal averageTicket, long pendingPayment,
                              long toPrepare, long ready, long shipped, long delivered, long cancelled, long overdue,
                              Map<String, Long> byState) {
}

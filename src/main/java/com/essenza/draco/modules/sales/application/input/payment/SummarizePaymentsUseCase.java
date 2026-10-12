package com.essenza.draco.modules.sales.application.input.payment;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentFilter;
import com.essenza.draco.modules.sales.application.dto.payment.PaymentsSummaryDto;

/** Totales de pagos por medio y estado (mismos filtros del listado). */
public interface SummarizePaymentsUseCase {

    PaymentsSummaryDto summarize(PaymentFilter filter);
}

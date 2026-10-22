package com.essenza.draco.modules.billing.application.dto.invoice;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Totales de facturación del filtro: facturado, notas crédito, neto e impuestos. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoicesSummaryDto {
    private Long invoices;
    private Long creditNotes;
    private Long pending;
    private BigDecimal invoicedTotal;
    private BigDecimal creditedTotal;
    private BigDecimal netTotal;
    private BigDecimal taxTotal;
    private BigDecimal creditedTax;
}

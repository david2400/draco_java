package com.essenza.draco.modules.billing.application.dto.invoice;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Impuestos agrupados por tarifa (como en la representación gráfica). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTaxDto {
    private String taxCode;
    private String taxKind;
    private BigDecimal taxRate;
    private BigDecimal taxBase;
    private BigDecimal taxAmount;
}

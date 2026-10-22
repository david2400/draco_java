package com.essenza.draco.modules.billing.application.dto.invoice;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLineDto {
    private Long id;
    private Integer position;
    private Long orderLineId;
    private String skuCode;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private String taxCode;
    private String taxKind;
    private BigDecimal taxRate;
    private BigDecimal taxBase;
    private BigDecimal taxAmount;
    private BigDecimal total;
}

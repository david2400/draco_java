package com.essenza.draco.modules.sales.application.dto.product_order;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderDto extends AuditInfoDto {
    private Long id;
    private Integer quantity;
    private BigDecimal discount;
    private BigDecimal subtotal;
    private BigDecimal total;
    private Long productId;
    private Long orderId;
    private Long skuId;
    private String skuCode;
    private String productName;
    private BigDecimal unitPrice;
    /** Impuesto incluido en el total (congelado): tarifa, código, porcentaje, base y valor. */
    private Long taxRateId;
    private String taxCode;
    private BigDecimal taxRate;
    private BigDecimal taxBase;
    private BigDecimal taxAmount;
    /** V16: parte del descuento global de la orden (ya descontada del total) y unidades preparadas. */
    private BigDecimal orderDiscount;
    private Integer pickedQuantity;
}

package com.essenza.draco.modules.sales.domain.dto.product_order;

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
}

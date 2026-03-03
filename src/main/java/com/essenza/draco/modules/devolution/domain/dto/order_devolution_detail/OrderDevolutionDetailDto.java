package com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail;

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
public class OrderDevolutionDetailDto extends AuditInfoDto {
    private Long id;
    private Integer quantity;
    private Integer receivedQuantity;
    private BigDecimal unitPrice;
    private BigDecimal refundAmount;
    private String condition;
    private BigDecimal restockingFee;
    private Long orderDevolutionId;
    private Long productOrderId;
    private String observation;
}

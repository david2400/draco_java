package com.essenza.draco.modules.devolution.domain.dto.order_devolution_detail;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDevolutionDetailDto {
    @NotNull
    @Positive
    private Integer quantity;
    private Integer receivedQuantity;
    private BigDecimal unitPrice;
    private BigDecimal refundAmount;
    private String condition;
    private BigDecimal restockingFee;
    @NotNull
    @Positive
    private Long orderDevolutionId;
    @NotNull
    @Positive
    private Long productOrderId;
    @NotBlank
    private String observation;
}

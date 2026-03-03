package com.essenza.draco.modules.devolution.domain.dto.order_devolution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDevolutionDto {
    @NotBlank
    private String observation;
    private String state; // optional on create, defaults to "P"
    @NotNull
    @Positive
    private Long motiveDevolutionId;
    @NotNull
    @Positive
    private Long orderId;

    // Optional operational fields
    private Long returnMethodId;
    private Long refundMethodId;
    private BigDecimal totalRefundAmount;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String receivedBy;
    private LocalDateTime receivedAt;
    private String inspectionNotes;
    private String externalReference;
}

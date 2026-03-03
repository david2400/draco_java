package com.essenza.draco.modules.devolution.domain.dto.order_devolution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDevolutionDto extends AuditInfoDto {
    private Long id;
    private String observation;
    private String state;
    private Long motiveDevolutionId;
    private Long orderId;

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

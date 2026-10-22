package com.essenza.draco.modules.devolution.application.dto.order_devolution;

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

    /** V10: bodega de reingreso, rechazo, inspección y reembolso. */
    private Long warehouseId;
    private String rejectedReason;
    private String rejectedBy;
    private LocalDateTime rejectedAt;
    private String inspectedBy;
    private LocalDateTime inspectedAt;
    /** Dinero devuelto; total_refund_amount es el valor acreditado al cliente. */
    private BigDecimal refundedAmount;
    private String refundedBy;
    private LocalDateTime refundedAt;
    private String refundDetail;
    /** Acciones del flujo disponibles (approve, reject, receive, inspect, refund, edit_lines). */
    private java.util.List<String> actions;
    /** Líneas (solo en el detalle). */
    private java.util.List<com.essenza.draco.modules.devolution.application.dto.order_devolution_detail.OrderDevolutionDetailDto> details;
}

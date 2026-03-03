package com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.OrderEntity;
import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_devolutions")
public class OrderDevolutionEntity extends AuditInfo {

    private final String orderDevolution = "orderDevolution";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order_devolution")
    private Long id;

    @Column(nullable = false)
    private String observation;

    @Column(nullable = false, length = 1)
    private String state = "P";

    @Column(name = "return_method_id")
    private Long returnMethodId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "return_method_id", insertable = false, updatable = false)
    private ReturnMethodEntity returnMethod;

    @Column(name = "refund_method_id")
    private Long refundMethodId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refund_method_id", insertable = false, updatable = false)
    private RefundMethodEntity refundMethod;

    @Column(name = "total_refund_amount", precision = 15, scale = 2)
    private BigDecimal totalRefundAmount;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "received_by", length = 100)
    private String receivedBy;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "inspection_notes", length = 255)
    private String inspectionNotes;

    @Column(name = "external_reference", length = 50)
    private String externalReference;

    @Column(name = "motive_devolution_id", nullable = false)
    private Long motiveDevolutionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "motive_devolution_id", insertable = false, updatable = false)
    private MotiveDevolutionEntity motiveDevolution;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderEntity order;

    @OneToMany(mappedBy = orderDevolution, fetch = FetchType.LAZY)
    private List<OrderDevolutionDetailEntity> orderDevolutionDetail;

    @OneToMany(mappedBy = orderDevolution, fetch = FetchType.LAZY)
    private List<OrderDevolutionEvidenceEntity> orderDevolutionEvidences;

}



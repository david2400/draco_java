package com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_devolution_evidences")
public class OrderDevolutionEvidenceEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order_devolution_evidence")
    private Long id;

    @Column(name = "order_devolution_id", nullable = false)
    private Long orderDevolutionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_devolution_id", insertable = false, updatable = false)
    private OrderDevolutionEntity orderDevolution;

    @Column(name = "evidence_type", nullable = false, length = 50)
    private String evidenceType;

    @Column(name = "resource_url", nullable = false, length = 255)
    private String resourceUrl;

    @Column(length = 255)
    private String description;

    @Column(name = "recorded_by", length = 100)
    private String recordedBy;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
}

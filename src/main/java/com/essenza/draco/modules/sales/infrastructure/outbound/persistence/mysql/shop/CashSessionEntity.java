package com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "cash_sessions")
public class CashSessionEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cash_session")
    private Long id;

    @Column(name = "cash_register_id", nullable = false)
    private Long cashRegisterId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "opened_by", nullable = false, length = 120)
    private String openedBy;

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @Column(name = "opening_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal openingAmount = BigDecimal.ZERO;

    @Column(name = "opening_notes", length = 500)
    private String openingNotes;

    @Column(name = "closed_by", length = 120)
    private String closedBy;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "expected_cash", precision = 15, scale = 2)
    private BigDecimal expectedCash;

    @Column(name = "counted_cash", precision = 15, scale = 2)
    private BigDecimal countedCash;

    @Column(name = "cash_difference", precision = 15, scale = 2)
    private BigDecimal cashDifference;

    @Column(name = "closing_notes", length = 500)
    private String closingNotes;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;
}

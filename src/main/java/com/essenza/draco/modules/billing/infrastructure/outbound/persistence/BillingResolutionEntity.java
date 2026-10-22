package com.essenza.draco.modules.billing.infrastructure.outbound.persistence;

import java.time.LocalDate;

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
@Table(name = "billing_resolutions")
public class BillingResolutionEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_billing_resolution")
    private Long id;

    @Column(name = "kind", nullable = false, length = 20)
    private String kind;

    @Column(name = "prefix", nullable = false, length = 10)
    private String prefix = "";

    @Column(name = "resolution_number", length = 40)
    private String resolutionNumber;

    @Column(name = "resolution_date")
    private LocalDate resolutionDate;

    @Column(name = "technical_key", length = 120)
    private String technicalKey;

    @Column(name = "from_number", nullable = false)
    private Long fromNumber;

    @Column(name = "to_number", nullable = false)
    private Long toNumber;

    @Column(name = "next_number", nullable = false)
    private Long nextNumber;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "environment", nullable = false, length = 20)
    private String environment = "TEST";

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;
}

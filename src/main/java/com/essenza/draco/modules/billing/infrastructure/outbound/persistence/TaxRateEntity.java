package com.essenza.draco.modules.billing.infrastructure.outbound.persistence;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "tax_rates")
public class TaxRateEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tax_rate")
    private Long id;

    @Column(name = "code", nullable = false, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "kind", nullable = false, length = 20)
    private String kind;

    @Column(name = "rate", nullable = false, precision = 6, scale = 3)
    private BigDecimal rate = BigDecimal.ZERO;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "position", nullable = false)
    private Integer position = 0;
}

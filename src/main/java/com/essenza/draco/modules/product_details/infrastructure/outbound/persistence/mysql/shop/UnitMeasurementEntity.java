package com.essenza.draco.modules.product_details.infrastructure.outbound.persistence.mysql.shop;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "units_measurement")
public class UnitMeasurementEntity extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unit_measurement")
    private Long id;

    /** Único entre no borradas (columna generada {@code name_active}, V7). */
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 20)
    private String dimension;

    @Column(nullable = false, precision = 24, scale = 12)
    private java.math.BigDecimal factor;

    @Column(name = "is_base", nullable = false)
    private Boolean base;

    @Column(name = "display_decimals", nullable = false)
    private Integer decimals;

    @Column(nullable = false)
    private Boolean active;

    @ManyToMany(mappedBy = "unitMeasurements")
    private Set<FeatureEntity> features;
}

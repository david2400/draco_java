package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.persistence.mysql.shop;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shipping_costs")
public class ShippingCostEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_shipping_cost")
    private Long id;

    @Column(name = "carrier_id", nullable = false)
    private Long carrierId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "carrier_id", insertable = false, updatable = false)
    private CarrierEntity carrier;

    @Column(name = "origin_address", nullable = false, length = 500)
    private String originAddress;

    @Column(name = "destination_address", nullable = false, length = 500)
    private String destinationAddress;

    @Column(name = "distance", precision = 10, scale = 2)
    private BigDecimal distance;

    @Column(name = "weight", precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(name = "volume", precision = 10, scale = 3)
    private BigDecimal volume;

    @Column(name = "calculated_cost", precision = 10, scale = 2)
    private BigDecimal calculatedCost;

    @Column(name = "calculation_method", length = 100)
    private String calculationMethod;

    @Column(name = "is_estimated", nullable = false)
    private Boolean isEstimated;
}

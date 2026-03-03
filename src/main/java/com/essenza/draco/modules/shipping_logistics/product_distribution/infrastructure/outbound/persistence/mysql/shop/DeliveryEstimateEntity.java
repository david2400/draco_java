package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.persistence.mysql.shop;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "delivery_estimates")
public class DeliveryEstimateEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_delivery_estimate")
    private Long id;

    @Column(name = "carrier_id", nullable = false)
    private Long carrierId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "carrier_id", insertable = false, updatable = false)
    private CarrierEntity carrier;

    @Column(name = "shipping_cost_id")
    private Long shippingCostId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shipping_cost_id", insertable = false, updatable = false)
    private ShippingCostEntity shippingCost;

    @Column(name = "origin_address", nullable = false, length = 500)
    private String originAddress;

    @Column(name = "destination_address", nullable = false, length = 500)
    private String destinationAddress;

    @Column(name = "shipment_date", nullable = false)
    private LocalDateTime shipmentDate;

    @Column(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    @Column(name = "min_delivery_date")
    private LocalDate minDeliveryDate;

    @Column(name = "max_delivery_date")
    private LocalDate maxDeliveryDate;

    @Column(name = "estimated_days")
    private Integer estimatedDays;

    @Column(name = "calculation_method", length = 100)
    private String calculationMethod;

    @Column(name = "is_business_days_only", nullable = false)
    private Boolean isBusinessDaysOnly;
}

package com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop;


import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.ProductOrderEntity;
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
@Table(name = "order_devolution_details")
public class OrderDevolutionDetailEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order_devolution_detail")
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "received_quantity")
    private Integer receivedQuantity;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "refund_amount", precision = 15, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "order_condition", length = 20)
    private String orderCondition;

    @Column(name = "restocking_fee", precision = 15, scale = 2)
    private BigDecimal restockingFee;

    @Column(name = "order_devolution_id", nullable = false)
    private Long orderDevolutionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_devolution_id", insertable = false, updatable = false)
    private OrderDevolutionEntity orderDevolution;

    @Column(name = "product_order_id", nullable = false)
    private Long productOrderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_order_id", insertable = false, updatable = false)
    private ProductOrderEntity productOrder;

    @Column(nullable = false)
    private String observation;


}



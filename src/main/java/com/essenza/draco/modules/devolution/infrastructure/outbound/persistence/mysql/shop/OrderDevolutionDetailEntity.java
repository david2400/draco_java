package com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop;


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

    // Sin asociación JPA hacia la línea de orden (módulo sales): se referencia solo por id (Fase 1, fronteras entre módulos).

    @Column(nullable = false)
    private String observation;

    /** V10: SKU, producto y nombre congelados de la línea de la orden. */
    @Column(name = "sku_id")
    private Long skuId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "sku_code", length = 100)
    private String skuCode;

    @Column(name = "product_name", length = 255)
    private String productName;

    /** Decisión de la inspección: las unidades recibidas vuelven al stock. */
    @Column(name = "restock")
    private Boolean restock;

    @Column(name = "restocked_quantity", nullable = false)
    private Integer restockedQuantity = 0;

    @PrePersist
    void defaultsV10() {
        if (restockedQuantity == null) {
            restockedQuantity = 0;
        }
    }


}



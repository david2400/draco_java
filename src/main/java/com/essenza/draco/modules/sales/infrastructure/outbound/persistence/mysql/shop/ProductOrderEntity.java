package com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop;

import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.OrderDevolutionDetailEntity;
import com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.persistence.mysql.shop.DispatchDetailEntity;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductEntity;
import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_orders")
public class ProductOrderEntity extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product_order")
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double discount;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double total;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity product;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderEntity order;

    @OneToMany(mappedBy = "productOrder", fetch = FetchType.LAZY)
    private List<OrderDevolutionDetailEntity> orderDevolution;

    @OneToMany(mappedBy = "productOrder", fetch = FetchType.LAZY)
    private List<DispatchDetailEntity> dispatchDetail;

}

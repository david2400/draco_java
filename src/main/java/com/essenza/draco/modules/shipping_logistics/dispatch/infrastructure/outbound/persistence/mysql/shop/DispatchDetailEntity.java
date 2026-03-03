package com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.persistence.mysql.shop;

import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.ProductOrderEntity;
import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dispatch_details")
public class DispatchDetailEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispatch_detail")
    private Long id;

    @Column(name = "dispatch_product_id", nullable = false)
    private Long dispatchProductId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dispatch_product_id", insertable = false, updatable = false)
    private DispatchProductEntity dispatchProduct;

    @Column(name = "product_order_id", nullable = false)
    private Long productOrderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_order_id", insertable = false, updatable = false)
    private ProductOrderEntity productOrder;

}


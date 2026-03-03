package com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop;

import com.essenza.draco.modules.devolution.infrastructure.outbound.persistence.mysql.shop.OrderDevolutionEntity;
import com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.persistence.mysql.shop.DispatchProductEntity;
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
@Table(name = "orders")
public class OrderEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private Long id;

    @Column(name = "complementary_order")
    private String complementaryOrder;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private String state;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<ProductOrderEntity> productOrder;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDevolutionEntity> orderDevolution;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<DispatchProductEntity> dispatchProduct;

}

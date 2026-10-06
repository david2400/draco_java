package com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop;

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

    @Column(nullable = false, precision = 15, scale = 2)
    private java.math.BigDecimal discount;

    @Column(nullable = false, precision = 15, scale = 2)
    private java.math.BigDecimal subtotal;

    @Column(nullable = false, precision = 15, scale = 2)
    private java.math.BigDecimal total;

    /** SKU vendido y foto congelada al crear la línea (Fase 5). */
    @Column(name = "sku_id")
    private Long skuId;

    @Column(name = "sku_code", length = 64)
    private String skuCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private java.math.BigDecimal unitPrice;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    // Sin asociación JPA hacia producto del catálogo: se referencia solo por id (Fase 1, fronteras entre módulos).

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderEntity order;

    // Sin asociación JPA hacia devoluciones (módulo devolution): se referencia solo por id (Fase 1, fronteras entre módulos).

    // Sin asociación JPA hacia detalle de despacho (módulo shipping_logistics): se referencia solo por id (Fase 1, fronteras entre módulos).

}

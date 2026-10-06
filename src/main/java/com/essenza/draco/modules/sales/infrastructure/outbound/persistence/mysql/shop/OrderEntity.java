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
@Table(name = "orders")
public class OrderEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private Long id;

    @Column(name = "complementary_order")
    private String complementaryOrder;

    @Column(nullable = false, precision = 15, scale = 2)
    private java.math.BigDecimal total;

    /** Fase 5: la orden reserva y descuenta stock (las anteriores no). */
    @Column(name = "stock_managed", nullable = false)
    @lombok.Builder.Default
    private Boolean stockManaged = false;

    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;

    @Column(nullable = false)
    private String state;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<ProductOrderEntity> productOrder;

    // Sin asociación JPA hacia devoluciones (módulo devolution): se referencia solo por id (Fase 1, fronteras entre módulos).

    // Sin asociación JPA hacia despachos (módulo shipping_logistics): se referencia solo por id (Fase 1, fronteras entre módulos).

}

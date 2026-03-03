package com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inventory_movements")
public class InventoryMovementEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movement")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity product;

    @Column(name = "from_warehouse_id")
    private Long fromWarehouseId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_warehouse_id", insertable = false, updatable = false)
    private WarehouseEntity fromWarehouse;

    @Column(name = "to_warehouse_id")
    private Long toWarehouseId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_warehouse_id", insertable = false, updatable = false)
    private WarehouseEntity toWarehouse;

    @Column(nullable = false, length = 20)
    private String type; // ENTRY, EXIT, TRANSFER

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 255)
    private String reason;
}

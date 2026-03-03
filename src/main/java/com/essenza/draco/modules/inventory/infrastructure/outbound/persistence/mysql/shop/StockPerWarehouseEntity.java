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
@Table(name = "stock_per_warehouse",
        uniqueConstraints = @UniqueConstraint(name = "uk_spw_product_warehouse", columnNames = {"product_id", "warehouse_id"}))
public class StockPerWarehouseEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false, unique = true)
    private ProductEntity product;

    @Column(name = "warehouse_id", nullable = false, unique = true)
    private Long warehouseId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false, unique = true)
    private WarehouseEntity warehouse;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "min_threshold", nullable = false)
    private Integer minThreshold;
}

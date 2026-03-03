package com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.persistence.mysql.shop;

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
@Table(name = "trackings")
public class TrackingEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trancking")
    private Long id;

    @Column(name = "dispatch_product_id", nullable = false)
    private Long dispatchProductId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dispatch_product_id", insertable = false, updatable = false)
    private DispatchProductEntity dispatchProduct;


}

package com.essenza.draco.modules.product_details.infrastructure.outbound.persistence.mysql.shop;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductEntity;
import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_features")
@IdClass(ProductFeatureEntity.PK.class)
public class ProductFeatureEntity extends AuditInfo {
    @Id
    @Column(name = "product_id")
    private Long productId;

    @Id
    @Column(name = "feature_id")
    private Long featureId;

    @Column(nullable = false)
    private Double value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "feature_id", insertable = false, updatable = false)
    private FeatureEntity feature;

    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class PK implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long productId;
        private Long featureId;
    }
}

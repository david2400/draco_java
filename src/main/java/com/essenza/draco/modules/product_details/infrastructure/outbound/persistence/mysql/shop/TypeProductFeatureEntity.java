package com.essenza.draco.modules.product_details.infrastructure.outbound.persistence.mysql.shop;

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
@Table(name = "type_product_features",
        uniqueConstraints = { @UniqueConstraint( columnNames = {"type_product_id", "feature_id"} )}
)
public class TypeProductFeatureEntity extends AuditInfo {
    @Id
    @Column(name = "type_product_id")
    private Long typeProductId;

    @Column(name = "feature_id", nullable = false)
    private Long featureId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_product_id", insertable = false, updatable = false)
    private TypeProductEntity typeProduct;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "feature_id", insertable = false, updatable = false)
    private FeatureEntity feature;
}

package com.essenza.draco.modules.recommendations.infrastructure.outbound.persistence.mysql;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "product_recommendations")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecommendationEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recommendation")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", length = 180)
    private String productName;

    @Column(name = "product_image_url", length = 255)
    private String productImageUrl;

    @Column(name = "product_price")
    private BigDecimal productPrice;

    @Column(name = "recommendation_type", length = 40)
    private String recommendationType;

    @Column(name = "score", precision = 6, scale = 4)
    private BigDecimal score;

    @Column(length = 255)
    private String reason;

    @Column
    private Integer position;

    @Column(name = "is_clicked")
    private Boolean isClicked;

    @Column(name = "is_purchased")
    private Boolean isPurchased;

    @Column(length = 40)
    private String context;
}

package com.essenza.draco.modules.advanced_features.infrastructure.outbound.persistence.mysql;

import com.essenza.draco.shared.common.domain.entity.AuditInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "personalization_profiles")
public class PersonalizationProfileEntity extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personalization_profile")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "segment", nullable = false, length = 30)
    private String segment;

    @Column(name = "status", length = 50)
    private String status;

    @Lob
    @Column(name = "context_metadata_json", columnDefinition = "LONGTEXT")
    private String contextMetadataJson;

    @Lob
    @Column(name = "recommended_products_json", columnDefinition = "LONGTEXT")
    private String recommendedProductsJson;

    @Lob
    @Column(name = "dynamic_pricing_json", columnDefinition = "LONGTEXT")
    private String dynamicPricingJson;

    @Lob
    @Column(name = "personalized_content_json", columnDefinition = "LONGTEXT")
    private String personalizedContentJson;

    @Lob
    @Column(name = "personalized_offers_json", columnDefinition = "LONGTEXT")
    private String personalizedOffersJson;

    @Lob
    @Column(name = "ui_personalization_json", columnDefinition = "LONGTEXT")
    private String uiPersonalizationJson;

    @Lob
    @Column(name = "purchase_intent_json", columnDefinition = "LONGTEXT")
    private String purchaseIntentJson;

    @Column(name = "personalization_score", precision = 5, scale = 4)
    private BigDecimal personalizationScore;

    @Column(name = "last_analysis_at")
    private Instant lastAnalysisAt;
}

package com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
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
public class PersonalizationProfileDto extends AuditInfoDto {

    private Long id;
    private Long customerId;
    private String sessionId;
    private String segment;
    private String status;
    private String contextMetadataJson;
    private String recommendedProductsJson;
    private String dynamicPricingJson;
    private String personalizedContentJson;
    private String personalizedOffersJson;
    private String uiPersonalizationJson;
    private String purchaseIntentJson;
    private BigDecimal personalizationScore;
    private Instant lastAnalysisAt;
}

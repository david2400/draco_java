package com.essenza.draco.modules.advanced_features.domain.dto.personalization_profile;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePersonalizationProfileDto {

    private Long customerId;

    @Size(max = 100, message = "Session id cannot exceed 100 characters")
    private String sessionId;

    @Pattern(regexp = "VIP|FREQUENT_BUYER|PRICE_SENSITIVE|BROWSER|NEW_CUSTOMER|CHURNING",
            message = "Segment must be one of VIP, FREQUENT_BUYER, PRICE_SENSITIVE, BROWSER, NEW_CUSTOMER or CHURNING")
    private String segment;

    @Size(max = 50, message = "Status cannot exceed 50 characters")
    private String status;

    @Size(max = 4000, message = "Context metadata cannot exceed 4000 characters")
    private String contextMetadataJson;

    @Size(max = 4000, message = "Recommended products payload cannot exceed 4000 characters")
    private String recommendedProductsJson;

    @Size(max = 4000, message = "Dynamic pricing payload cannot exceed 4000 characters")
    private String dynamicPricingJson;

    @Size(max = 4000, message = "Personalized content payload cannot exceed 4000 characters")
    private String personalizedContentJson;

    @Size(max = 4000, message = "Personalized offers payload cannot exceed 4000 characters")
    private String personalizedOffersJson;

    @Size(max = 4000, message = "UI personalization payload cannot exceed 4000 characters")
    private String uiPersonalizationJson;

    @Size(max = 4000, message = "Purchase intent payload cannot exceed 4000 characters")
    private String purchaseIntentJson;

    @DecimalMin(value = "0.0", inclusive = true, message = "Personalization score must be at least 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "Personalization score cannot exceed 1")
    private BigDecimal personalizationScore;

    private Instant lastAnalysisAt;
}

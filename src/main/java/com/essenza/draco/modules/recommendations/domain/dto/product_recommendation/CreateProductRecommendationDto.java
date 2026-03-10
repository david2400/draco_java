package com.essenza.draco.modules.recommendations.domain.dto.product_recommendation;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRecommendationDto {

    @NotNull
    private Long customerId;

    @NotNull
    private Long productId;

    @Size(max = 180)
    private String productName;

    @Size(max = 255)
    private String productImageUrl;

    private BigDecimal productPrice;

    @Size(max = 40)
    private String recommendationType;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal score;

    @Size(max = 255)
    private String reason;

    private Integer position;

    private Boolean isClicked;

    private Boolean isPurchased;

    @Size(max = 40)
    private String context;
}

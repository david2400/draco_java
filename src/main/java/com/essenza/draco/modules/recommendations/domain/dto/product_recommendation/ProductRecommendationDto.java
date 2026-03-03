package com.essenza.draco.modules.recommendations.domain.dto.product_recommendation;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecommendationDto extends AuditInfoDto {
    private Long id;
    private Long customerId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal productPrice;
    private String recommendationType; // COLLABORATIVE, CONTENT_BASED, TRENDING, CROSS_SELL, UP_SELL
    private BigDecimal score; // Puntuación de relevancia (0.0 - 1.0)
    private String reason; // Razón de la recomendación
    private Integer position; // Posición en la lista de recomendaciones
    private Boolean isClicked;
    private Boolean isPurchased;
    private String context; // HOME, PRODUCT_PAGE, CART, CHECKOUT
}

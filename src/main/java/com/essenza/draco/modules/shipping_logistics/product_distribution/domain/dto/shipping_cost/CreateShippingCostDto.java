package com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShippingCostDto {
    @NotNull
    @Positive
    private Long carrierId;
    
    @NotBlank
    private String originAddress;
    
    @NotBlank
    private String destinationAddress;
    
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal distance;
    
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal weight;
    
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal volume;
    
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal calculatedCost;
    
    private String calculationMethod;
    
    @NotNull
    private Boolean isEstimated;
}

package com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryEstimateDto {
    @NotNull
    @Positive
    private Long carrierId;
    
    @Positive
    private Long shippingCostId;
    
    @NotBlank
    private String originAddress;
    
    @NotBlank
    private String destinationAddress;
    
    @NotNull
    private LocalDateTime shipmentDate;
    
    private LocalDate estimatedDeliveryDate;
    
    private LocalDate minDeliveryDate;
    
    private LocalDate maxDeliveryDate;
    
    @Min(1)
    private Integer estimatedDays;
    
    private String calculationMethod;
    
    @NotNull
    private Boolean isBusinessDaysOnly;
}

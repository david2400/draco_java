package com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCarrierDto {
    @NotBlank
    private String name;
    
    @NotBlank
    private String code;
    
    @Email
    private String contactEmail;
    
    private String contactPhone;
    
    private String website;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal baseRate;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal ratePerKm;
    
    @NotNull
    @Min(1)
    private Integer maxDeliveryDays;
    
    @NotNull
    private Boolean isActive;
}

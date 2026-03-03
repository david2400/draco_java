package com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShippingCostDto extends CreateShippingCostDto {
    @NotNull
    @Positive
    private Long id;
}

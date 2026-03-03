package com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier;

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
public class UpdateCarrierDto extends CreateCarrierDto {
    @NotNull
    @Positive
    private Long id;
}
